using System.Numerics;

namespace Inking.Otd
{
    public struct InkingPacket
    {
        public ulong Flags;
        public Vector2 Position;
        public Vector2 Tilt;
        public uint Pressure;
        public uint HoverDistance;
        public ulong PenButtons;
        public ulong AuxButtons;
    }
}
