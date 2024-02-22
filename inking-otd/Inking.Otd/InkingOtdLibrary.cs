using Microsoft.Extensions.DependencyInjection;
using OpenTabletDriver;
using OpenTabletDriver.Plugin.Tablet;
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
            delegate*<nint, InkingPacket, void> packetCallback
        )
        {
            if (InitializationState) return false;
            
            try
            {
                var collection = new DriverServiceCollection().AddTransient<Driver>();
                var provider = collection.BuildServiceProvider();
                driver = provider.GetRequiredService<Driver>();

                driver.CompositeDeviceHub.DevicesChanged += (_, _) => internalDevicesChanged(connectStateCallback, packetCallback);
                internalDetectDevices(connectStateCallback, packetCallback);

                InitializationState = true;
            } catch (Exception ex)
            {
                Console.Error.WriteLine(ex);
                return false;
            }

            return true;
        }

        private static void internalDevicesChanged(
            delegate*<nint, bool, void> connectStateCallback,
            delegate*<nint, InkingPacket, void> packetCallback
        )
        {
            Task.Delay(50).Wait();
            internalDetectDevices(connectStateCallback, packetCallback);
        }

        private static void internalDetectDevices(
            delegate*<nint, bool, void> connectStateCallback,
            delegate*<nint, InkingPacket, void> packetCallback
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
                            IAbsolutePositionReport? pos = report is IAbsolutePositionReport a ? a : null;
                            ITabletReport? tab = report is ITabletReport b ? b : null;
                            IAuxReport? aux = report is IAuxReport c ? c : null;
                            ITiltReport? tilt = report is ITiltReport d ? d : null;
                            IProximityReport? hover = report is IProximityReport e ? e : null;
                            IEraserReport? eraser = report is IEraserReport f ? f : null;

                            bool shouldSend = pos != null || tab != null || aux != null || tilt != null || hover != null || eraser != null;
                            if (!shouldSend) return;

                            uint pressure = tab != null ? tab.Pressure : 0u;
                            ulong penButtons = tab != null ? boolArrayToFlags(tab.PenButtons) : 0ul;
                            ulong auxButtons = aux != null ? boolArrayToFlags(aux.AuxButtons) : 0ul;
                            ulong flags =
                                (eraser != null && eraser.Eraser? 0b00000001ul : 0ul);

                            InkingPacket packet = new()
                            {
                                Flags = flags,
                                Position = pos != null ? pos.Position : new System.Numerics.Vector2(0, 0),
                                Tilt = tilt != null ? tilt.Tilt : new System.Numerics.Vector2(0, 0),
                                Pressure = pressure,
                                HoverDistance = hover != null ? hover.HoverDistance : (pressure == 0u ? 1u : 0u),
                                PenButtons = penButtons,
                                AuxButtons = auxButtons,
                            };

                            packetCallback(convertString(serial), packet);
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

        private static ulong boolArrayToFlags(bool[] bools)
        {
            ulong flags = 0ul;
            for (int i = 0; i < bools.Length; i++) flags |= (bools[i]? 1ul : 0ul) << i;
            return flags;
        }

        [UnmanagedCallersOnly(EntryPoint = "get_tablet_info")]
        public static bool GetTabletInfo(nint serialStrPtr, InkingTabletSpec spec)
        {
            string? serial = Marshal.PtrToStringUTF8(serialStrPtr);
            if (serial == null) return false;
            if (!connectedDevices.TryGetValue(serial, out InputDevice? device)) return false;
            if (device == null) return false;

            var tabletConfig = device.Configuration;
            var tabletSpec = tabletConfig.Specifications;

            spec.TabletName = convertString(tabletConfig.Name);
            spec.MaxPressure = tabletSpec.Pen.MaxPressure;
            spec.PhysicalSize = new System.Numerics.Vector2()
            {
                X = tabletSpec.Digitizer.Width,
                Y = tabletSpec.Digitizer.Height
            };
            spec.InputSize = new System.Numerics.Vector2()
            {
                X = tabletSpec.Digitizer.MaxX,
                Y = tabletSpec.Digitizer.MaxY
            };
            spec.PenButtons = tabletSpec.Pen.Buttons?.ButtonCount ?? 0;
            spec.AuxButtons = tabletSpec.AuxiliaryButtons?.ButtonCount ?? 0;
            return true;
        }
    }
}
