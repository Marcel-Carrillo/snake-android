package com.snakegame.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.snakegame.engine.SnakeEngine;
import com.snakegame.engine.GameState;

public class GameScreen implements Screen {
    private SpriteBatch batch;
    private BitmapFont font;
    private SnakeEngine snakeEngine;
    private Rectangle btnUp, btnDown, btnLeft, btnRight;
    private Rectangle restartButtonBounds;

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        snakeEngine = new SnakeEngine(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        int btnSize = 100;
        int margin = 50;
        btnUp = new Rectangle(Gdx.graphics.getWidth() / 2f - btnSize / 2f, margin + btnSize * 2, btnSize, btnSize);
        btnDown = new Rectangle(Gdx.graphics.getWidth() / 2f - btnSize / 2f, margin, btnSize, btnSize);
        btnLeft = new Rectangle(Gdx.graphics.getWidth() / 2f - btnSize * 1.5f, margin + btnSize, btnSize, btnSize);
        btnRight = new Rectangle(Gdx.graphics.getWidth() / 2f + btnSize * 0.5f, margin + btnSize, btnSize, btnSize);

        // Botón de reinicio centrado
        restartButtonBounds = new Rectangle(
                Gdx.graphics.getWidth() / 2f - 75,
                Gdx.graphics.getHeight() / 2f - 30,
                150, 60
        );
    }

    @Override
    public void render(float delta) {
        handleInput();
        Gdx.gl.glClearColor(0, 0.2f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        snakeEngine.update(delta);

        batch.begin();
        snakeEngine.render(batch);

        if (snakeEngine.getGameState() == GameState.RUNNING) {
            //font.draw(batch, "Controles: ↑ ↓ ← →", 20, 40);
            font.draw(batch, "↑", btnUp.x + 35, btnUp.y + 65);
            font.draw(batch, "↓", btnDown.x + 35, btnDown.y + 65);
            font.draw(batch, "←", btnLeft.x + 35, btnLeft.y + 65);
            font.draw(batch, "→", btnRight.x + 35, btnRight.y + 65);
        } else if (snakeEngine.getGameState() == GameState.GAME_OVER) {
            font.draw(batch, "¡Has perdido!", Gdx.graphics.getWidth() / 2f - 60, Gdx.graphics.getHeight() / 2f + 60);
            font.draw(batch, "[ Reintentar ]", restartButtonBounds.x + 10, restartButtonBounds.y + 35);
            // 👇 Muestra los puntos alcanzados
            font.draw(batch, "Puntos: " + snakeEngine.getScore(),
                    Gdx.graphics.getWidth() / 2f - 40,
                    Gdx.graphics.getHeight() / 2f + 30);
        }
        font.draw(batch, "Puntos: " + snakeEngine.getScore(), 20, Gdx.graphics.getHeight() - 20);
        font.draw(batch, "Récord: " + snakeEngine.getHighScore(), 20, Gdx.graphics.getHeight() - 50);
        font.draw(batch, "Vidas: " + snakeEngine.getLives(), 20, Gdx.graphics.getHeight() - 80);
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Si estamos en Game Over y pulsamos el botón de reinicio
            if (snakeEngine.getGameState() == GameState.GAME_OVER) {
                if (restartButtonBounds.contains(x, y)) {
                    snakeEngine.resetGame();
                }
                return;
            }

            // Controles con botones
            if (btnUp.contains(x, y)) snakeEngine.setDirection(new Vector2(0, 20));
            else if (btnDown.contains(x, y)) snakeEngine.setDirection(new Vector2(0, -20));
            else if (btnLeft.contains(x, y)) snakeEngine.setDirection(new Vector2(-20, 0));
            else if (btnRight.contains(x, y)) snakeEngine.setDirection(new Vector2(20, 0));
            else {
                // Movimiento con toques libres en pantalla
                Vector2 head = snakeEngine.getHeadPosition();
                float dx = x - head.x;
                float dy = y - head.y;

                if (Math.abs(dx) > Math.abs(dy)) {
                    // Movimiento horizontal
                    if (dx > 0) snakeEngine.setDirection(new Vector2(20, 0));
                    else snakeEngine.setDirection(new Vector2(-20, 0));
                } else {
                    // Movimiento vertical
                    if (dy > 0) snakeEngine.setDirection(new Vector2(0, 20));
                    else snakeEngine.setDirection(new Vector2(0, -20));
                }
            }
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        batch.dispose();
        font.dispose();
        snakeEngine.dispose();
    }
}
