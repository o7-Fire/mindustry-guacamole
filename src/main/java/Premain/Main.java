package Premain;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.gl.FrameBuffer;
import arc.scene.ui.Dialog;
import arc.util.Log;
import arc.util.ScreenUtils;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Icon;
import mindustry.mod.Mod;

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

		final long[] start = {Time.millis()};
		Events.run(EventType.Trigger.draw, ()-> {
			if (Time.millis() - start[0] < 1000) return;
			;
			start[0] = Time.millis();
			//check if world is loaded and not paused
			if (Vars.state.isGame() && !Vars.state.isPaused() && Vars.renderer.minimap.getTexture() != null) {

				ScreenUtils.saveScreenshot(crop);
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
			for(int x = 0; x < 2; x++){
				for(int y = 0; y < 2; y++){
					pixmap.set(i + x, i2 + y, color);
				}
			}
		}else if(type == 2){//draw a pixel
			// X
			pixmap.set(i, i2, color);


		}
	}
}
