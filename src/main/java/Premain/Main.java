package Premain;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.graphics.*;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.gl.FrameBuffer;
import arc.scene.ui.Dialog;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Icon;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.mod.Mod;

import java.nio.ByteBuffer;

public class Main extends Mod {
	boolean stat;
	static Texture white = null;
	static FrameBuffer buffer = null;
	public static float color = Color.whiteFloatBits;
	public static float stroke = 1f;
	public static Fi epicFolder = null;

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

		final long[] start = {Time.millis()};
		Events.run(EventType.Trigger.draw, () -> {
			if (Time.millis() - start[0] < 1000) return;
			;
			start[0] = Time.millis();

			//check if world is loaded and not paused
			if (Vars.state.isGame() && !Vars.state.isPaused() && Vars.renderer.minimap.getTexture() != null) {
				Draw.draw(Draw.z(), () -> {
					if (buffer == null) {
						buffer = new FrameBuffer(w, h);
						//clear the buffer - some OSs leave garbage in it
						buffer.begin(Pal.darkerMetal);
						buffer.end();
					}
				});
				Draw.draw(Draw.z(), () -> {
					Tmp.m1.set(Draw.proj());
					Draw.proj(0, 0, w, h);
					buffer.begin();
					Draw.color(color);
					Lines.stroke(stroke);
					int centerX = w / 2;
					int centerY = h / 2;
					//Draw unit here
					for (Unit unit : Groups.unit) {
						//skip if outside range
						if (unit.dst(Vars.player) > 100) continue;
						Draw.color(unit.team.color);
						Draw.rect(unit.icon(), unit.x - centerX, unit.y - centerY);
					}
					//Draw bullet
					for (Bullet bullet : Groups.bullet) {
						//skip if outside range
						if (bullet.dst(Vars.player) > 100) continue;
						Draw.color(bullet.team.color);
						bullet.draw();
					}
					buffer.end();
					Draw.proj(Tmp.m1);
					Draw.reset();
				});

				Draw.draw(Draw.z(), () -> {
					if (buffer != null) {
						//copy buffer to pixmap
						final Pixmap pixmap = new Pixmap(w, h);
						final ByteBuffer buf = pixmap.getPixels();
						buffer.getTexture().bind();
						Core.gl.glReadPixels(0, 0, w, h, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, buf);
						//save pixmap to file
						PixmapIO.writePng(crop, pixmap);
						pixmap.dispose();
					}
				});

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
