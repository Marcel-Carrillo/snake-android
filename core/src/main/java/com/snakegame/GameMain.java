package com.snakegame;

import com.badlogic.gdx.Game;
import com.snakegame.screens.GameScreen;

public class GameMain extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}