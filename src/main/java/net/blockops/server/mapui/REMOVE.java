package net.blockops.server.mapui;

import net.blockops.server.mapui.art.MapUIColors;
import net.blockops.server.mapui.component.components.*;
import net.blockops.server.mapui.map.MapUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.map.MapCanvas;
import org.bukkit.plugin.java.JavaPlugin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class REMOVE extends JavaPlugin implements Listener {

    private MapUIManager mapUIManager;

    @Override
    public void onEnable() {
        mapUIManager = new MapUIManager(this, (short) 0);
        mapUIManager.init();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().equalsIgnoreCase("/testMapUI")) {
            MapUI mapUI = mapUIManager.getMapUIFromRegisteredPlayer(event.getPlayer());
            if (mapUI != null) {
                mapUI.getViewController().clearComponents();
                mapUI.getViewController().setDirty(true);
            } else {
                mapUI = new MapUI(mapUIManager, event.getPlayer());
                mapUI.init();
                mapUIManager.registerPlayerMapUI(event.getPlayer(), mapUI);
            }
            final MapUI mapUIReference = mapUI; // For inner classes

            MapBackground mapBackground = new MapBackground(MapUIColors.TRANSPARENT);
            mapUI.getViewController().setMapBackground(mapBackground);

            byte x = MapUIColors.WHITE;
            byte[][] cursorPixels = new byte[][]{
                    {x, x, x},
                    {x, x, 0},
                    {x, 0, x}
            };
            MapCursor mapCursor = new MapCursor();
            mapCursor.setCursorPixels(cursorPixels, false);
            mapCursor.setCursorSensitivityBounds(0, 0, 2, 2);
            mapUI.getViewController().setMapCursor(mapCursor);

            MapText mapText = new MapText(
                    "Attachment", MapUIColors.DARK_RED, mapUIManager.getSmallMinecraftFont(), 0, 0);
            mapText.centerHorizontallyTo(0, 128);
            mapText.centerVerticallyTo(0, 64);

            Rectangle buttonBounds = new Rectangle(mapText.getComponentBounds());
            buttonBounds.grow(2, 1);

            MapButton mapButton = new MapButton(buttonBounds) {
                @Override
                public void draw(MapCanvas mapCanvas) {
                    if (isHovered()) {
                        drawRectangle(mapCanvas, getComponentBounds(), MapUIColors.RED);
                    } else {
                        drawRectangle(mapCanvas, getComponentBounds(), MapUIColors.DARK_GRAY);
                    }
                    mapText.draw(mapCanvas);
                }

                @Override
                public void onHoverEnter(MapUI mapUI, MapCursor mapCursor) {
                    mapUI.getViewController().setDirty(true);
                }

                @Override
                public void onHoverExit(MapUI mapUI, MapCursor mapCursor) {
                    mapUI.getViewController().setDirty(true);
                }

                @Override
                public void onClick(MapUI mapUI, MapCursor mapCursor) {
                    Bukkit.broadcastMessage(mapUI.getPlayer().getName() + " clicked MapButton!");
                }
            };
            mapUI.getViewController().addComponent(mapButton);

            BufferedImage barrierImage = null;
            BufferedImage checkmarkImage = null;
            BufferedImage tateImage = null;
            try {
                barrierImage = ImageIO.read(this.getClass().getResourceAsStream("/barrier.png"));
                checkmarkImage = ImageIO.read(this.getClass().getResourceAsStream("/checkmark.png"));
                //tateImage = ImageIO.read(this.getClass().getResourceAsStream("/tate.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (barrierImage != null) {
                MapImage cancelMapImage = new MapImage(barrierImage, 0, 0);
                cancelMapImage.centerVerticallyTo(64, 64);
                cancelMapImage.centerHorizontallyTo(0, 64);

                Rectangle checkmarkButtonBounds = new Rectangle(cancelMapImage.getComponentBounds());
                checkmarkButtonBounds.grow(2, 2);

                MapButton cancelButton = new MapButton(checkmarkButtonBounds) {
                    @Override
                    public void draw(MapCanvas mapCanvas) {
                        if (isHovered()) {
                            drawRectangle(mapCanvas, getComponentBounds(), MapUIColors.WHITE);
                        } else {
                            drawRectangle(mapCanvas, getComponentBounds(), MapUIColors.DARK_GRAY);
                        }
                        cancelMapImage.draw(mapCanvas);
                    }

                    public void onHoverEnter(MapUI mapUI, MapCursor mapCursor) {
                        mapUI.getViewController().setDirty(true);
                    }

                    @Override
                    public void onHoverExit(MapUI mapUI, MapCursor mapCursor) {
                        mapUI.getViewController().setDirty(true);
                    }

                    @Override
                    public void onClick(MapUI mapUI, MapCursor mapCursor) {
                        Bukkit.broadcastMessage(mapUI.getPlayer().getName() + " clicked cancel!");
                    }
                };
                mapUI.getViewController().addComponent(cancelButton);
            }
            if (checkmarkImage != null) {
                MapImage checkmarkMapImage = new MapImage(checkmarkImage, 0, 0);
                checkmarkMapImage.centerVerticallyTo(64, 64);
                checkmarkMapImage.centerHorizontallyTo(64, 64);

                Rectangle checkmarkButtonBounds = new Rectangle(checkmarkMapImage.getComponentBounds());
                checkmarkButtonBounds.grow(2, 2);
                MapButton checkmarkButton = new MapButton(checkmarkButtonBounds) {
                    @Override
                    public void draw(MapCanvas mapCanvas) {
                        if (isHovered()) {
                            drawRectangle(mapCanvas, getComponentBounds(), MapUIColors.WHITE);
                        } else {
                            drawRectangle(mapCanvas, getComponentBounds(), MapUIColors.DARK_GRAY);
                        }
                        checkmarkMapImage.draw(mapCanvas);
                    }

                    public void onHoverEnter(MapUI mapUI, MapCursor mapCursor) {
                        mapUI.getViewController().setDirty(true);
                    }

                    @Override
                    public void onHoverExit(MapUI mapUI, MapCursor mapCursor) {
                        mapUI.getViewController().setDirty(true);
                    }

                    @Override
                    public void onClick(MapUI mapUI, MapCursor mapCursor) {
                        Bukkit.broadcastMessage(mapUI.getPlayer().getName() + " clicked checkmark!");
                    }
                };
                mapUI.getViewController().addComponent(checkmarkButton);
            }

            if (tateImage != null) {
                MapImage tateMapImage = new MapImage(tateImage, 0, 0);
                mapUI.getViewController().addComponent(tateMapImage);
            }

            //mapUI.getViewController().addComponent(new MapComponent() {
            //    @Override
            //    public void update() {
            //        if (mapUIReference.getPlayerController().didPlayerDirectionChange()) {
            //            mapUIReference.getViewController().setDirty(true);
            //        }
            //    }
            //
            //    @Override
            //    public void draw(MapCanvas mapCanvas) {
            //        PlayerController playerController = mapUIReference.getPlayerController();
            //        drawLine(mapCanvas, 0, 0, playerController.getCursorX(), playerController.getCursorY(), 2,
            //                MapUIColors.RED);
            //    }
            //});
            mapUI.getViewController().setDirty(true);

            mapUI.open(ChatColor.BOLD.toString() + ChatColor.BLUE + "Sneak to Exit", true, -1);
        }
    }

}
