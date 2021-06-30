using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Timers;

namespace Generator
{
    class Program
    {
        static Generator generator = new Generator();

        static void Main(string[] args)
        {
            Timer timer = new Timer(3000);
            timer.Elapsed += Handler;
            timer.Enabled = true;
            timer.Start();

            Console.ReadKey();
            

        }

        private static void Handler(Object sender, ElapsedEventArgs e)
        {
            Weapon weapon = generator.generateWeapon(20);

            weapon.status();
            Console.WriteLine();
            Console.WriteLine();
        }
    }
}
