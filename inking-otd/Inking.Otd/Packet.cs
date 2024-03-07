using System.Numerics;

namespace Inking.Otd
{
    public struct Packet
    {
        public PenState States;
        public Vector2 Position;
        public Vector2 Tilt; // always degree
        public uint Pressure;
        public uint HoverDistance;
        public ulong PenButtons;
        public ulong AuxButtons;
        public ulong Timestamp;
    }
}
