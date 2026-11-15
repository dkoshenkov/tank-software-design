package ru.mipt.bit.platformer.input.command;

public final class ToggleHealthBarsCommand implements Runnable {
    private boolean visible;

    @Override
    public void run() {
        visible = !visible;
    }

    public boolean isVisible() {
        return visible;
    }
}
