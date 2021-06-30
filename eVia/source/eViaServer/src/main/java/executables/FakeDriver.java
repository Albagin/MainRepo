package executables;

import utility.FakeDriverCommunication;

public class FakeDriver {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            FakeDriverCommunication fakeDriver = new FakeDriverCommunication();
            fakeDriver.start();
        }
    }
}
