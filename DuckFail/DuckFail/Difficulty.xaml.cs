using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;
using System.Windows.Threading;

namespace DuckFail
{
    /// <summary>
    /// Logika interakcji dla klasy Difficulty.xaml
    /// </summary>
    public partial class Difficulty : Window
    {
        private  MainWindow main;


        private Random r = new Random();
        protected int time = 100;//miliseconds
        protected int pauseTime = 2000;//miliseconds
        protected int mainTime = 20;//seconds
        private DispatcherTimer timer = new DispatcherTimer();
        private DispatcherTimer mainTimer = new DispatcherTimer();
        private int l = 0;
        private int maxDucks = 10;

        private const int width = 900;
        private const int height = 500;

        private int _score = 0;
        private int _HS = 0;

        public int score
        {
            get => _score;
            set => _score++;
        }

        public int HS
        {
            get => _HS;
            set => _HS = score;
        }



        public Difficulty(MainWindow main)
        {
            this.main = main;
            InitializeComponent();
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            pauseTime = 2000;
            time = 3000;
            mainTime = 18;
            maxDucks = 7;
            StartTheGame();
        }

        private void Button_Click_1(object sender, RoutedEventArgs e)
        {
            pauseTime = 1000;
            time = 1500;
            mainTime = 13;
            maxDucks = 10;
            StartTheGame();
        }

        private void Button_Click_2(object sender, RoutedEventArgs e)
        {
            pauseTime = 500;
            time = 1200;
            mainTime = 22;
            maxDucks = 20;
            StartTheGame();
        }

        private void Button_Click_3(object sender, RoutedEventArgs e)
        {
            pauseTime = 350;
            time = 1000;
            mainTime = 16;
            maxDucks = 30;
            StartTheGame();
        }

        private void Back_Click(object sender, RoutedEventArgs e)
        {
            main.Show();
            this.Close();
        }

        private void generateDuck()
        {
            int a = r.Next(0, width);
            int b = r.Next(0, height);


            Duck d = new Duck((Difficulty)GetWindow(this), a, b, time);
            d.Show();

        }

        private void handler(Object obj, EventArgs arg)
        {
            generateDuck();
            if (l == maxDucks) timer.IsEnabled = false;
            else l++;
        }

        private void mainHandler(Object obj, EventArgs args)
        {
            String rank;

            double p = score * 1.0 / maxDucks * 1.0;

            if (p >= 1) rank = "Perfect!!!";
            else if (p > 0.9) rank = "Great!!";
            else if (p > 0.5) rank = "Good!";
            else if (p > 0.2) rank = "Poorly";
            else rank = "Patethic...";

            MessageBox.Show(("Your score: " + score + "\nYour rank: " + rank), "Score", MessageBoxButton.OK);
            mainTimer.IsEnabled = false;
            main.Show();
            GetWindow(this).Close();
            
        }

        protected void StartTheGame()
        {
            timer.Tick += new EventHandler(handler);
            timer.Interval = new TimeSpan(0, 0, 0, 0, pauseTime);

            mainTimer.Tick += new EventHandler(mainHandler);
            mainTimer.Interval = new TimeSpan(0, 0, mainTime);

            timer.Start();
            mainTimer.Start();
            GetWindow(this).Hide();
        }
    }
}
