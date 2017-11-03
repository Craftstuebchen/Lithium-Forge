/*
 * MIT License
 *
 * Copyright (c) 2017 NickAc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package net.nickac.lithium.frontend.mod.ui.renders;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.nickac.lithium.backend.controls.impl.LSlider;
import net.nickac.lithium.backend.other.LithiumConstants;
import net.nickac.lithium.backend.other.objects.Point;
import net.nickac.lithium.backend.other.objects.Rectangle;
import net.nickac.lithium.backend.other.rendering.ILithiumControlRenderer;
import net.nickac.lithium.frontend.mod.network.LithiumMessage;
import net.nickac.lithium.frontend.mod.ui.ButtonRenderer;
import net.nickac.lithium.frontend.mod.ui.NewLithiumGUI;
import net.nickac.lithium.frontend.mod.utils.MiscUtils;
import net.nickac.lithium.frontend.mod.utils.ModCoderPackUtils;

/**
 * Created by NickAc for Lithium!
 */
public class SliderRenderer implements ILithiumControlRenderer<LSlider, GuiScreen> {
	private final int SLIDER_WIDTH = 10;

	@Override
	public void renderLithiumControl(LSlider control, GuiScreen gui) {
		Point loc = NewLithiumGUI.centerControl(control);

		Rectangle rect = new Rectangle(loc.getX(), loc.getY(), control.getSize().getWidth(), control.getSize().getHeight());
		Rectangle rect2 = rect.inflate(-1, -1);

		//Draw border and then inside
		Gui.drawRect(rect.getLeft(), rect.getTop(), rect.getRight(), rect.getBottom(), (int) control.getBorderColor().getHexColor());
		Gui.drawRect(rect2.getLeft(), rect2.getTop(), rect2.getRight(), rect2.getBottom(), (int) control.getBackgroundColor().getHexColor());

		int startX = rect.getLeft();
		int endX = rect.getRight() - SLIDER_WIDTH;

		//Then draw the handle
		ButtonRenderer.drawButton(
				gui,
				"",
				0,
				0,
				MiscUtils.ConvertRange(control.getMinValue(), control.getMaxValue(), startX, endX, control.getValue()),
				rect.getY() + 1,
				SLIDER_WIDTH,
				rect.getHeight() - 3
		);

	}

	@Override
	public void mouseClick(LSlider control, GuiScreen gui, int mouseX, int mouseY, int mouseButton) {
		Point point = NewLithiumGUI.centerControl(control);
		Point mouseLoc = new Point(mouseX, mouseY);

		Rectangle rect = new Rectangle(point, control.getSize());

		if (rect.inflate(-2, -1).contains(mouseLoc)) {
			Point clickedPoint = new Point(mouseX - rect.getX(), mouseY - rect.getY());
			changeSlider(control, clickedPoint);
			ModCoderPackUtils.playButtonPressSound();
		}
	}

	public void changeSlider(LSlider control, Point clickedPoint) {
		//Looks confusing, but let me explain...
		//We turn the clicked position to relative coordinates...
		//Then we convert them to a value on the slider
		//Finally we "normalize" the value by respecting maximum an minimum value
		control.setValue(
				Math.max(
						control.getMinValue(),
						Math.min(
								MiscUtils.ConvertRange(
										1,
										control.getSize().getWidth() - SLIDER_WIDTH,
										control.getMinValue(),
										control.getMaxValue(),
										clickedPoint.getX()
								),
								control.getMaxValue()
						)
				)
		);
		ModCoderPackUtils.sendLithiumMessageToServer(
				new LithiumMessage(
						LithiumConstants.LITHIUM_SLIDER_VALUE_CHANGED + "|" + control.getUUID() + "|" + control.getValue()
				)
		);
	}

	@Override
	public void mouseClickMove(LSlider control, GuiScreen gui, int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		Point point = NewLithiumGUI.centerControl(control);
		Point mouseLoc = new Point(mouseX, mouseY);

		Rectangle rect = new Rectangle(point, control.getSize());

		if (rect.inflate(-2, -1).contains(mouseLoc)) {
			Point clickedPoint = new Point(mouseX - rect.getX(), mouseY - rect.getY());
			changeSlider(control, clickedPoint);
		}
	}

}
