using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Generator
{
	 class Generator
	{
		static Random r = new Random();
		static int ID = 0;

		public Weapon generateWeapon(int charLevel)
		{
			int rating = r.Next(Convert.ToInt32(charLevel * 100 * 0.75), Convert.ToInt32(charLevel * 100 * 1.25));
			int level = rating / 100;

			Components.Type type = typeGen();
			Components.Element element = elementGen();
			String name = nameGen(rating, type, element);


			Weapon weapon = new Weapon();

			weapon.Value = rating;
			weapon.Level = levelGen(level);
			weapon.MinDmg = minDmgGen(rating/50);
			weapon.MaxDmg = maxDmgGen(rating/50);
			weapon.Type = type;
			weapon.Element = element;
			weapon.Name = name;
			weapon.Id = ID;
			ID++;
			

			return weapon;
		}

		private int levelGen(int lvl)
		{
			int level = r.Next(Convert.ToInt32(lvl * 0.8), Convert.ToInt32(lvl * 1.2));
			return level;
		}

		private int maxDmgGen(int rating)
		{
			int dmg = r.Next(Convert.ToInt32(rating * 1.05), Convert.ToInt32(rating * 1.15));

			return dmg;
		}

		private int minDmgGen(int rating)
		{
			int dmg = r.Next(Convert.ToInt32(rating * 0.85), Convert.ToInt32(rating * 0.95));

			return dmg;
		}

		private Components.Element elementGen()
		{
			Random r = new Random();

			Components.Element element;

			int dummy = r.Next(0, 99);

			if (dummy < Components.elementChance[0]) element = Components.Element.Neutral;
			else if (dummy < Components.elementChance[1]) element = Components.Element.Fire;
			else if (dummy < Components.elementChance[2]) element = Components.Element.Ice;
			else element = Components.Element.Lightning;

			return element;
		}

		private Components.Type typeGen()
		{
			Random r = new Random();

			Components.Type type;

			int dummy = r.Next(0, 99);

			if (dummy < Components.typeChance[0]) type = Components.Type.Sword;
			else if (dummy < Components.typeChance[1]) type = Components.Type.Mace;
			else type = Components.Type.Axe;

			return type;
		}

		private String nameGen(int rating, Components.Type type, Components.Element element)
		{
			Components.Name subname;

			if (rating > 8000) subname = Components.Name.Mythic;
			else if (rating > 5500) subname = Components.Name.Rare;
			else if (rating > 2000) subname = Components.Name.Common;
			else subname = Components.Name.Weak;

			String name = (subname + " " + type + " " + (element.Equals(Components.Element.Neutral) ? "" : "of " + element));

			return name;
		}
	}
}
