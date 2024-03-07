using System.Numerics;

namespace Inking.Otd
{
    public struct TabletInfo
    {
        public nint TabletName;
        public uint MaxPressure;
        public Vector2 PhysicalSize;
        public Vector2 InputSize;
        public uint PenButtons;
        public uint AuxButtons;
    }
}
