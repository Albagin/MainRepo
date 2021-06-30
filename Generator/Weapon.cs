using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Generator
{
     class Weapon
    {
        private int id;
        private string name;
        private int value;
        private int level;
        private Components.Type type;

        private int minDmg;
        private int maxDmg;

        private Components.Element element;

        public Weapon(string name, int value, int level, Components.Type type, int minDmg, int maxDmg, Components.Element element)
        {
            this.name = name;
            this.value = value;
            this.level = level;
            this.Type = type;
            this.minDmg = minDmg;
            this.maxDmg = maxDmg;
            this.element = element;
        }

        public Weapon() { }

        public int Id { get => id; set => id = value; }
        public string Name { get => name; set => name = value; }
        public int Value { get => value; set => this.value = value; }
        public int Level { get => level; set => level = value; }
        public int MinDmg { get => minDmg; set => minDmg = value; }
        public int MaxDmg { get => maxDmg; set => maxDmg = value; }
        internal Components.Element Element { get => element; set => element = value; }
        public Components.Type Type { get => type; set => type = value; }

        public void status()
        {
            Console.WriteLine(name);
            Console.WriteLine("Level: " + level);
            Console.WriteLine("Damage: " + minDmg + "-" + maxDmg);
            Console.WriteLine("Rating: " + value);
        }
    }
}
