package org.example.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

public class MainMenu {

    public enum MenuState {
        MAIN, STAGE_SELECT, HOW_TO, INGAME
    }
    
    private MenuState currentState = MenuState.MAIN;

    private static final double SCREEN_WIDTH = 896; 
    private static final double SCREEN_HEIGHT = 640;
    
    private static final double BTN_WIDTH = 192; 
    private static final double BTN_HEIGHT = 64; 
    private static final double BTN_GAP = 20;

    private Image logoImage;
    private final List<MenuButton> mainButtons = new ArrayList<>();
    private final List<MenuButton> stageButtons = new ArrayList<>();

    public MainMenu() {
        try {
            logoImage = new Image(getClass().getResourceAsStream("/asset/logo.png"));
        } catch (Exception e) {
            System.err.println("Gagal load logo.png, pastikan file ada di /asset/logo.png");
        }
        initButtons();
    }

    private void initButtons() {
        double centerX = (SCREEN_WIDTH - BTN_WIDTH) / 2;
        
        double startY = 280;

        mainButtons.add(new MenuButton("/asset/button/start.png", "/asset/button/start_select.png", centerX, startY, 
            () -> currentState = MenuState.STAGE_SELECT));
            
        mainButtons.add(new MenuButton("/asset/button/howto.png", "/asset/button/howto_select.png", centerX, startY + BTN_HEIGHT + BTN_GAP, 
            () -> currentState = MenuState.HOW_TO));
            
        mainButtons.add(new MenuButton("/asset/button/exit.png", "/asset/button/exit_select.png", centerX, startY + (2 * (BTN_HEIGHT + BTN_GAP)), 
            () -> System.exit(0)));

        stageButtons.add(new MenuButton("/asset/button/stage1.png", "/asset/button/stage1_select.png", centerX, (SCREEN_HEIGHT - BTN_HEIGHT) / 2, 
            () -> {
                System.out.println("Stage 1 Started!");
                currentState = MenuState.INGAME;
            }));
    }

    public void update(double mouseX, double mouseY) {
        List<MenuButton> current = getCurrentButtons();
        if (current != null) {
            for (MenuButton btn : current) {
                btn.checkHover(mouseX, mouseY);
            }
        }
    }

    public void onClick(double mouseX, double mouseY) {
        if (currentState == MenuState.HOW_TO) {
            currentState = MenuState.MAIN; 
            return;
        }

        List<MenuButton> current = getCurrentButtons();
        if (current != null) {
            for (MenuButton btn : current) {
                if (btn.isHovered) {
                    btn.action.run();
                }
            }
        }
    }

    public void draw(GraphicsContext g) {

        if (currentState == MenuState.MAIN) {
            if (logoImage != null) {
                double logoW = 400;
                double logoH = logoImage.getHeight() * (logoW / logoImage.getWidth());
                double logoX = (SCREEN_WIDTH - logoW) / 2;
                double logoY = 50; 

                g.drawImage(logoImage, logoX, logoY, logoW, logoH);
            } else {
                g.setFill(Color.web("#643c14"));
                g.setFont(Font.font("Arial", FontWeight.BOLD, 48));
                g.setTextAlign(TextAlignment.CENTER);
                g.fillText("KITCHEN CHAOS", SCREEN_WIDTH / 2, 150);
            }
        } 
        else if (currentState == MenuState.STAGE_SELECT) {
            g.setFill(Color.WHITE);
            g.setFont(Font.font("Arial", FontWeight.BOLD, 40));
            g.setTextAlign(TextAlignment.CENTER);
            g.fillText("SELECT STAGE", SCREEN_WIDTH / 2, 150);
        }

        if (currentState == MenuState.HOW_TO) {
            g.setFill(Color.rgb(0, 0, 0, 0.7));
            g.fillRoundRect(200, 100, SCREEN_WIDTH - 400, 400, 20, 20);

            g.setFont(Font.font("Arial", 20));
            g.setFill(Color.WHITE);
            g.setTextAlign(TextAlignment.CENTER);
            g.fillText("TUTORIAL\n\nMove: WASD\nInteract: V\nPick Up/Drop: C\nChop: Hold V", SCREEN_WIDTH / 2, 250);
            
            g.setFill(Color.YELLOW);
            g.fillText("[ Click anywhere to Return ]", SCREEN_WIDTH / 2, 450);
        } else {
            List<MenuButton> current = getCurrentButtons();
            if (current != null) {
                for (MenuButton btn : current) {
                    btn.draw(g);
                }
            }
        }
    }

    private List<MenuButton> getCurrentButtons() {
        return switch (currentState) {
            case MAIN -> mainButtons;
            case STAGE_SELECT -> stageButtons;
            default -> null;
        };
    }

    public MenuState getState() { return currentState; }

    private static class MenuButton {
        Image imgNormal, imgSelect;
        double x, y;
        boolean isHovered = false;
        Runnable action;

        public MenuButton(String pathNormal, String pathSelect, double x, double y, Runnable action) {
            this.x = x;
            this.y = y;
            this.action = action;
            try {
                this.imgNormal = new Image(getClass().getResourceAsStream(pathNormal));
                this.imgSelect = new Image(getClass().getResourceAsStream(pathSelect));
            } catch (Exception e) {
                System.err.println("Failed to load button: " + pathNormal);
            }
        }

        void checkHover(double mx, double my) {
            isHovered = (mx >= x && mx <= x + BTN_WIDTH && my >= y && my <= y + BTN_HEIGHT);
        }

        void draw(GraphicsContext g) {
            Image toDraw = isHovered ? imgSelect : imgNormal;
            if (toDraw != null) {
                g.drawImage(toDraw, x, y, BTN_WIDTH, BTN_HEIGHT);
            } else {
                g.setFill(isHovered ? Color.BLUE : Color.GRAY);
                g.fillRect(x, y, BTN_WIDTH, BTN_HEIGHT);
            }
        }
    }
}