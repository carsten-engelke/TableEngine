package org.engine.menu;

import org.engine.TableEngine;
import org.engine.object.popup.PopupSlave;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class DialogFactory {

	static Stage createMessageDialog(final String text, final String button1,
			final Skin skin, final PopupSlave slave, final TableEngine t) {

		final Stage s = new Stage();
		final Table tab = new Table(skin);
		tab.add(new Label(text, skin)).fill().expand();
		tab.row();
		final TextButton button = new TextButton(button1, skin);
		button.addListener(new ChangeListener() {

			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				slave.receiveOrder(button1);
				t.switchUI(
						TableEngine.SHOW_UNIVERSE);
			}
		});
		tab.add(button).fill().expandX();
		s.addActor(tab);
		return s;
	}

	static Stage createQuestionDialog(final String text, final String button1,
			final String button2, final Skin skin, final PopupSlave slave,
			final TableEngine t) {
		final Stage s = new Stage();
		final Table tab = new Table(skin);
		tab.setFillParent(true);
		tab.add(new Label(text, skin)).fill().expand().colspan(2);
		tab.row();
		final TextButton buttonT1 = new TextButton(button1, skin);
		buttonT1.addListener(new ChangeListener() {

			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				slave.receiveOrder(button1);
				t.switchUI(
						TableEngine.SHOW_UNIVERSE);
			}
		});
		tab.add(buttonT1).fill().expandX();
		final TextButton buttonT2 = new TextButton(button2, skin);
		buttonT2.addListener(new ChangeListener() {

			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				slave.receiveOrder(button2);
				t.switchUI(
						TableEngine.SHOW_UNIVERSE);
			}
		});
		tab.add(buttonT2).fill().expandX();
		s.addActor(tab);
		return s;
	}
}
