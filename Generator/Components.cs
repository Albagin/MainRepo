using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Generator
{
    public static class Components
    {
        public enum Element { Neutral, Fire, Ice, Lightning };
        public static int[] elementChance = new int[3] { 25, 50, 75 };

        public enum Type {Sword,Axe,Mace };
        public static int[] typeChance = new int[3] { 25, 50, 75 };

        public enum Name {Weak, Common, Rare, Mythic };
    }
}
