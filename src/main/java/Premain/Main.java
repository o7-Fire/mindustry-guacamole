package Premain;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.graphics.*;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.GLFrameBuffer;
import arc.math.Mathf;
import arc.math.Rand;
import arc.scene.ui.Dialog;
import arc.util.Log;
import arc.util.ScreenUtils;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.mod.Mod;

import static mindustry.Vars.renderer;

public class Main extends Mod {
	boolean stat;
	static Texture white =  null;
	static FrameBuffer buffer = null;
	@Override
	public void init() {
		Log.infoTag("Example-Mods", "Hello World!");

		if (Vars.ui == null) return;
		Fi crop = new Fi("epic.png");
		Fi uncrop = new Fi("epic2.png");
		int w = 224;
		int h = 224;

		Events.run(EventType.Trigger.draw, ()->{
			//check if world is loaded and not paused
			if (Vars.state.isGame() && !Vars.state.isPaused() && Vars.renderer.minimap.getTexture() != null) {
				int x = Vars.player.tileX();
				int y = Vars.player.tileY();

				int centerX = x - w / 2;
				int centerY = y - h / 2;
				Pixmap pixmap, pixmap1;
				pixmap = Vars.renderer.minimap.getPixmap().flipY();

				//Draw unit here
				for(Unit unit : Groups.unit){
					drawEntity(centerX, centerY, pixmap, unit.tileX(), unit.tileY(), unit, unit.team() != Vars.player.team() ? 0 : 1);
				}
				//Draw bullets here
				for(Bullet bullet : Groups.bullet){
					drawEntity(centerX, centerY, pixmap, bullet.tileX(), bullet.tileY(), bullet, 2);
				}

				//PixmapIO.writePng(crop, pixmap);
				//Crop and chop
				pixmap1 = pixmap.crop(centerX, centerY, w, h);
				pixmap.dispose();

				//Flipping
				pixmap = pixmap1.flipY();
				pixmap1.dispose();

				//Flush
				PixmapIO.writePng(crop, pixmap);
				pixmap.dispose();

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
		Vars.ui.settings.game.row().table(t ->{
			t.check("Boolean [red][Test]", s->{
				stat = s;//assign to global ?
				Vars.ui.showInfo(stat+"");//toString()
			});
		}).growX();//stretch
	}

	private void drawEntity(int centerX, int centerY, Pixmap pixmap, int i, int i2, Teamc team, int type) {
		int x2 = i;
		int y2 = i2;
		Color color = team.team().color;



		if(type == 0){//draw triangle
			pixmap.set(x2, y2, color);
			pixmap.set(x2 + 1, y2, color);
			pixmap.set(x2, y2 + 1, color);
		}else if(type == 1){//draw rectangle
			// X X
			// X X
			for(int x = 0; x < 2; x++){
				for(int y = 0; y < 2; y++){
					pixmap.set(x2 + x, y2 + y, color);
				}
			}
		}else if(type == 2){//draw a pixel
			// X
			pixmap.set(x2, y2, color);


		}
	}
}
