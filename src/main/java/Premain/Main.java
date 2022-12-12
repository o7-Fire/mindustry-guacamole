package Premain;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.graphics.*;
import arc.graphics.gl.FrameBuffer;
import arc.scene.ui.Dialog;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Reflect;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.graphics.BlockRenderer;
import mindustry.mod.Mod;
import mindustry.world.Tile;
import mindustry.world.blocks.logic.CanvasBlock;
import mindustry.world.blocks.logic.LogicDisplay;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;

public class Main extends Mod {
	boolean stat;
	static Texture white = null;
	static FrameBuffer buffer = null;
	public static float color = Color.whiteFloatBits;
	public static float stroke = 1f;
	public static Fi epicFolder = null;
	public static Seq<Tile> tileview = null;
	@Override
	public void init() {
		Log.infoTag("Example-Mods", "Hello World!");
		epicFolder = new Fi("epic");
		if (!epicFolder.exists()) {
			epicFolder.mkdirs();
		}
		if (Vars.ui == null) return;
		int w = 224;
		int h = 224;
		tileview = Reflect.get(BlockRenderer.class, Vars.renderer.blocks, "tileview");
		final long[] start = {Time.millis()};
		Events.run(EventType.Trigger.postDraw, () -> {
			if (Time.millis() - start[0] < 2000) return;

			start[0] = Time.millis();

			//check if world is loaded and not paused
			if (Vars.state.isGame() && !Vars.state.isPaused()) {
				Seq<Tile> seq = new Seq<>(tileview);
				while (seq.size > 0) {
					Tile tile = seq.pop();
					Building b = tile.build;
					if (b == null) continue;
					if (b.block == null) continue;
					Pixmap pixmap = null;
					String hashCode = "null";

					if (b instanceof LogicDisplay.LogicDisplayBuild) {
						//get frame buffer and save to file
						LogicDisplay.LogicDisplayBuild build = (LogicDisplay.LogicDisplayBuild) b;
						if (build.buffer == null) continue;
						pixmap = new Pixmap(build.buffer.getWidth(), build.buffer.getHeight());
						final ByteBuffer buf = pixmap.getPixels();
						build.buffer.begin();
						Core.gl.glReadPixels(0, 0, build.buffer.getWidth(), build.buffer.getHeight(), GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, buf);
						build.buffer.end();
						hashCode =
								build.tileX() + "-" + build.tileY() + "-LogicDisplay" + "(" + build.block.size + "x" +
										build.block.size + ")";

					}

					if (b instanceof CanvasBlock.CanvasBuild) {
						//get frame buffer and save to file
						CanvasBlock.CanvasBuild build = (CanvasBlock.CanvasBuild) b;
						CanvasBlock block = (CanvasBlock) build.block;
						//check for rectangular groups of canvas blocks
						HashSet<Building> buildings = new HashSet<>();
						buildings.add(build);
						ArrayList<Tile> tiles = new ArrayList<>();
						tiles.add(build.tile);
						HashSet<Tile> visited = new HashSet<>();
						while (!tiles.isEmpty()) {
							Tile t = tiles.remove(0);
							for (int i = 0; i < 4; i++) {
								Tile n = t.nearby(i);
								if (n == null) continue;
								Building nb = n.build;
								if (nb == null) continue;
								if (nb.block instanceof CanvasBlock && visited.add(n)) {
									buildings.add(nb);
									tiles.add(n);
									seq.remove(n);
								}
							}
						}
						//get the smallest rectangle that contains all canvas blocks
						int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
						for (Building b2 : buildings) {
							minX = Math.min(minX, b2.tileX());
							minY = Math.min(minY, b2.tileY());
							maxX = Math.max(maxX, b2.tileX() + b2.block.size);
							maxY = Math.max(maxY, b2.tileY() + b2.block.size);
						}
						//get the width and height of the canvas
						int canvasSize = block.canvasSize;
						int width = ((maxX - minX) / block.size) * canvasSize;
						int height = ((maxY - minY) / block.size) * canvasSize;
						hashCode = build.tileX() + "-" + build.tileY() + "-Canvas" + "(" + width + "x" + height + ")";
						//merge all canvases into one
						pixmap = new Pixmap(width, height);
						//Use building
						boolean flipY = true;//wtf ?!?!?!?
						boolean flipX = false;
						for (Building bb : buildings) {
							Tile t = bb.tile;
							CanvasBlock.CanvasBuild build1 = (CanvasBlock.CanvasBuild) t.build;
							int x = ((build1.tileX() - minX) / block.size) * canvasSize;
							int y = ((build1.tileY() - minY) / block.size) * canvasSize;
							//TODO optimize this
							Pixmap pixmap1 = build1.makePixmap();
							for (int i = 0; i < canvasSize; i++) {
								for (int j = 0; j < canvasSize; j++) {
									int color = pixmap1.get(i, j);
									if (color != 0) {
										int x1 = x + (flipX ? canvasSize - i - 1 : i);
										int y1 = y + (flipY ? canvasSize - j - 1 : j);
										pixmap.set(x1, y1, color);
									}
								}
							}
							pixmap1.dispose();
						}

					}
					if (pixmap != null) {
						Fi file = epicFolder.child("epic-" + hashCode + ".png");
						PixmapIO.writePng(file, pixmap);
						pixmap.dispose();
					}
				}
			}
		});
		//do you like lambda ?
		Core.settings.getBoolOnce("Test", () -> {//only run once btw
			Events.on(EventType.ClientLoadEvent.class, s -> {
				new Dialog("Test") {{
					if (Vars.mobile) buttons.button("Close", this::hide).growX();//mobile friendly on portrait
					else buttons.button("Close", this::hide).size(210f, 64f);//what with this magic number
					//garbage here
					cont.button("Yeet", Icon.image, () -> {
						Vars.ui.showInfo("Yeet");
					}).growX();//stretch
				}}.show();
			});
		});//sike how to color bracket
		Vars.ui.settings.game.row().table(t -> {
			t.check("Boolean [red][Test]", s -> {
				stat = s;//assign to global ?
				Vars.ui.showInfo(stat + "");//toString()
			});
		}).growX();//stretch
	}

	private void drawEntity(Pixmap pixmap, int i, int i2, Team team, int type, Team player) {
		//reduce white
		Color color = team.color.cpy().mul(0.9f);

		if (team != player) color.mul(0.8f);

		if (type == 0) {//draw triangle
			pixmap.set(i, i2, color);
			pixmap.set(i + 1, i2, color);
			pixmap.set(i, i2 + 1, color);
		} else if (type == 1) {//draw rectangle
			// X X
			// X X
			for (int x = 0; x < 2; x++) {
				for (int y = 0; y < 2; y++) {
					pixmap.set(i + x, i2 + y, color);
				}
			}
		} else if (type == 2) {//draw a pixel
			// X
			pixmap.set(i, i2, color);


		}
	}
}
