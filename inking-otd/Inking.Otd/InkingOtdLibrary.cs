using Microsoft.Extensions.DependencyInjection;
using OpenTabletDriver;
using OpenTabletDriver.Plugin.Tablet;
using System.Diagnostics;
using System.Numerics;
using System.Runtime.InteropServices;

namespace Inking.Otd
{
    public static unsafe class InkingOtdLibrary
    {
        private static bool InitializationState = false;
        private static Driver? driver = null;
        private static Dictionary<string, InputDevice> connectedDevices = new();
        private static Dictionary<string, nint> Strings = new();

        private static nint convertString(string str)
        {
            if (Strings.TryGetValue(str, out nint value)) return value;
            nint ptr = Marshal.StringToCoTaskMemUTF8(str);
            Strings.Add(str, ptr);
            return ptr;
        }

        [UnmanagedCallersOnly(EntryPoint = "initialize_driver")]
        public static bool InitializeDriver(
            delegate*<nint, bool, void> connectStateCallback,
            delegate*<nint, Packet, void> packetCallback
        )
        {
            if (InitializationState) return false;
            
            try
            {
                var collection = new DriverServiceCollection().AddTransient<Driver>();
                var provider = collection.BuildServiceProvider();
                driver = provider.GetRequiredService<Driver>();

                driver.CompositeDeviceHub.DevicesChanged += (_, _) => InternalDevicesChanged(connectStateCallback, packetCallback);
                InternalDetectDevices(connectStateCallback, packetCallback);

                InitializationState = true;
            } catch (Exception ex)
            {
                Console.Error.WriteLine(ex);
                return false;
            }

            return true;
        }

        private static void InternalDevicesChanged(
            delegate*<nint, bool, void> connectStateCallback,
            delegate*<nint, Packet, void> packetCallback
        )
        {
            // TODO kill 50ms delay
            Task.Delay(50).Wait();
            InternalDetectDevices(connectStateCallback, packetCallback);
        }

        private static void InternalDetectDevices(
            delegate*<nint, bool, void> connectStateCallback,
            delegate*<nint, Packet, void> packetCallback
        )
        {
            if (driver == null) return;
            driver.Detect();

            foreach (var devicesCollection in driver.InputDevices)
            {
                foreach (var device in devicesCollection.InputDevices)
                {
                    string serial = device.Endpoint.SerialNumber;

                    if (connectedDevices.TryAdd(serial, device))
                    {
                        void ReportsHandler(object? sender, IDeviceReport report)
                        {
                            if (report is DeviceReport) return;
                            packetCallback(convertString(serial), PacketFromReport(report));
                        }

                        void ConnectionHandler(object? sender, bool connected)
                        {
                            if (!connected)
                            {
                                device.Report -= ReportsHandler;
                                device.ConnectionStateChanged -= ConnectionHandler;
                                connectedDevices.Remove(serial);
                                connectStateCallback(convertString(serial), false);
                            }
                        }

                        device.Report += ReportsHandler;
                        device.ConnectionStateChanged += ConnectionHandler;
                        connectStateCallback(convertString(serial), true);
                    }
                }
            }
        }

        private static Packet PacketFromReport(IDeviceReport report)
        {
            uint pressure = 0;
            ulong penButtons = 0ul;
            ulong auxButtons = 0ul;

            if (report is ITabletReport tab)
            {
                pressure = tab.Pressure;
                penButtons = BoolArrayToFlags(tab.PenButtons);
            }

            if (report is IAuxReport aux)
            {
                auxButtons = BoolArrayToFlags(aux.AuxButtons);
            }

            PenState flags =
                (pressure > 0 ? PenState.PenDown : PenState.None) |
                (report is IEraserReport eraser && eraser.Eraser ? PenState.Eraser : PenState.None);

            Packet packet = new()
            {
                States = flags,
                Position = report is IAbsolutePositionReport pos ? pos.Position : new Vector2(0, 0),
                Tilt = report is ITiltReport tilt ? tilt.Tilt : new Vector2(0, 0),
                Pressure = pressure,
                HoverDistance = report is IProximityReport proximity
                    ? proximity.HoverDistance
                    : pressure > 0 ? 0u : 1u,
                PenButtons = penButtons,
                AuxButtons = auxButtons,
                Timestamp = GetNanoTime()
            };
            return packet;
        }

        // Mirrors System.nanoTime() from Java
        private static ulong GetNanoTime() => 10000ul * (ulong)Stopwatch.GetTimestamp() / TimeSpan.TicksPerMillisecond * 100ul;

        private static ulong BoolArrayToFlags(bool[] bools)
        {
            ulong flags = 0ul;
            for (int i = 0; i < bools.Length; i++) flags |= (bools[i]? 1ul : 0ul) << i;
            return flags;
        }

        [UnmanagedCallersOnly(EntryPoint = "get_tablet_info")]
        public static bool GetTabletInfo(nint serialStrPtr, TabletInfo spec)
        {
            string? serial = Marshal.PtrToStringUTF8(serialStrPtr);
            if (serial == null) return false;
            if (!connectedDevices.TryGetValue(serial, out InputDevice? device)) return false;
            if (device == null) return false;

            var tabletConfig = device.Configuration;
            var tabletSpec = tabletConfig.Specifications;

            spec.TabletName = convertString(tabletConfig.Name);
            spec.MaxPressure = tabletSpec.Pen.MaxPressure;
            spec.PhysicalSize = new Vector2() { X = tabletSpec.Digitizer.Width, Y = tabletSpec.Digitizer.Height };
            spec.InputSize = new Vector2() { X = tabletSpec.Digitizer.MaxX, Y = tabletSpec.Digitizer.MaxY };
            spec.PenButtons = tabletSpec.Pen.Buttons?.ButtonCount ?? 0;
            spec.AuxButtons = tabletSpec.AuxiliaryButtons?.ButtonCount ?? 0;
            return true;
        }
    }
}
