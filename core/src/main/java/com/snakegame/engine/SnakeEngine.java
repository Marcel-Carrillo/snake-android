package com.snakegame.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import java.util.*;

public class SnakeEngine {
    private final Texture segmentTexture;
    private final Texture foodTexture;
    private final List<Vector2> body;
    private Vector2 direction = new Vector2(20, 0);
    private float timer = 0f;
    private final float moveInterval = 0.2f;
    private Vector2 food;
    private final int width, height;
    private boolean alive = true;
    private GameState gameState = GameState.RUNNING;
    private int score = 0;
    private int highScore = 0;
    private Preferences prefs;
    private int lives = 3;
    private float speedMultiplier = 1.0f;


    public SnakeEngine(int screenWidth, int screenHeight) {
        segmentTexture = new Texture("segment.png");
        foodTexture = new Texture("food.png");
        width = screenWidth;
        height = screenHeight;
        body = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            body.add(new Vector2(200 - i * 20, 200));
        }
        spawnFood();
        prefs = Gdx.app.getPreferences("SnakeGamePrefs");
        highScore = prefs.getInteger("highScore", 0);
    }

    public void update(float delta) {
        if (!alive || body.isEmpty()) return;
        if(lives != 0){
            timer += delta * speedMultiplier;
            if (timer >= moveInterval) {
                timer = 0;
                Vector2 newHead = new Vector2(body.get(0)).add(direction);

                if (collidesWithWall(newHead) || collidesWithSelf(newHead)) {
                    lives--;
                    if (lives > 0) {
                        // Reiniciar la serpiente pero sin resetear score ni highScore
                        body.clear();
                        for (int i = 0; i < 5; i++) {
                            body.add(new Vector2(200 - i * 20, 200));
                        }
                        direction = new Vector2(20, 0);
                        alive = true;
                        gameState = GameState.RUNNING;
                        return;
                    } else {
                        alive = false;
                        gameState = GameState.GAME_OVER;
                        return;
                    }
                }

                body.add(0, newHead);

                if (newHead.epsilonEquals(food, 1f)) {
                    score++;
                    if (score > highScore) {
                        highScore = score;
                        prefs.putInteger("highScore", highScore);
                        prefs.flush();
                    }
                    spawnFood();
                    increaseSpeed();
                } else {
                    if (body.size() > 0) {
                        body.remove(body.size() - 1);
                    }
                }
            }
        }else{
            alive = false;
        }


    }


    public void render(SpriteBatch batch) {
        for (Vector2 part : body) {
            batch.draw(segmentTexture, part.x, part.y, 20, 20);
        }
        batch.draw(foodTexture, food.x, food.y, 20, 20);
    }

    public void setDirection(Vector2 newDir) {
        if (!newDir.cpy().add(direction).isZero()) {
            direction = newDir;
        }
    }

    private void spawnFood() {
        Random rand = new Random();
        int fx = rand.nextInt(width / 20) * 20;
        int fy = rand.nextInt(height / 20) * 20;
        food = new Vector2(fx, fy);
    }

    private boolean collidesWithWall(Vector2 pos) {
        return pos.x < 0 || pos.y < 0 || pos.x >= width || pos.y >= height;
    }

    private boolean collidesWithSelf(Vector2 pos) {
        for (int i = 1; i < body.size(); i++) {
            if (body.get(i).epsilonEquals(pos, 1f)) return true;
        }
        return false;
    }

    public void resetGame() {
        body.clear();
        for (int i = 0; i < 5; i++) {
            body.add(new Vector2(200 - i * 20, 200));
        }
        direction = new Vector2(20, 0);
        spawnFood();
        alive = true;
        gameState = GameState.RUNNING;
        score = 0;
        lives = 3;
        speedMultiplier = 1.0f;
    }

    public boolean isAlive() {
        return alive;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void dispose() {
        segmentTexture.dispose();
        foodTexture.dispose();
    }

    public Vector2 getHeadPosition() {
        return body.get(0);
    }

    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }

    public int getLives() {
        return lives;
    }

    public void increaseSpeed() {
        speedMultiplier += 0.05f; // Aumenta la velocidad un 5%
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }
}