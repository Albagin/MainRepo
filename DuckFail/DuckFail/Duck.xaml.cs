using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Timers;
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
    /// Logika interakcji dla klasy Duck.xaml
    /// </summary>
    public partial class Duck : Window
    {

        private DispatcherTimer timer;
        private Difficulty diff;

        public Duck(Difficulty diff, int a, int b, int time)
        {
            this.diff = diff;
            timer = new DispatcherTimer();
            timer.Interval = new TimeSpan(0, 0, 0, 0, time);

           this.Left = a;
            this.Top = b;

            InitializeComponent();
            timer.Tick += new EventHandler(Timeout);
            timer.Start();
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            diff.score++;
            GetWindow(this).Close();
        }

        private void Timeout(Object obj, EventArgs a)
        {
            //timer.Stop();
            timer.IsEnabled = false;
            GetWindow(this).Close();
        }
    }
}
