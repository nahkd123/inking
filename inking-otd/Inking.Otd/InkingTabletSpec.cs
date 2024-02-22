using System.Numerics;

namespace Inking.Otd
{
    public struct InkingTabletSpec
    {
        public nint TabletName;
        public uint MaxPressure;
        public Vector2 PhysicalSize;
        public Vector2 InputSize;
        public uint PenButtons;
        public uint AuxButtons;
    }
}
