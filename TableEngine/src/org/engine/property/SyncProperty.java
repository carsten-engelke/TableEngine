package org.engine.property;

import org.engine.Synchronizable;
import org.engine.utils.Array;

public class SyncProperty implements Property<Synchronizable> {

	private Synchronizable value;
	private Information i;

	public SyncProperty(String id, String tag, Flag f, Synchronizable value) {

		this.value = value;
		this.i = new Information(id, tag, f, Information.PropertiesToString(value
						.getProperties()));
	}

	@Override
	public Information info() {

		i.content = Information.PropertiesToString(value.getProperties());
		return i;
	}

	public Information infoFlaggedOnly() {

		Array<Property<?>> a = value.getPropertiesFlagged();
		if (a != null && a.getSize() > 0) {
			i.content = Information.PropertiesToString(a);
			return i;
		}
		return null;
	}

	@Override
	public void applySyncInfo(Information i) {

		if (i.id.equals(this.i.id)) {

			try {
				value.setPropertiesFromInformation(Information
						.StringToInformations(i.content));
				this.i.content = i.content;
				this.i.tag = i.tag;
			} catch (InformationArrayStringException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Synchronizable get() {

		return value;
	}

	@Override
	public void set(Synchronizable value) {

		this.value.setPropertiesFromInformation(Information
				.PropertiesToInformations(value.getProperties()));
		i.content = Information.PropertiesToString(value.getProperties());
	}

	@Override
	public Flag flag() {

		if (i.flag == Flag.NONE) {
			if (value.getPropertiesFlagged() != null
					&& value.getPropertiesFlagged().getSize() > 0) {
				i.flag = Flag.ADD_CHANGE;
			}
		}
		return i.flag;
	}

	@Override
	public void setFlag(Flag f) {

		this.i.flag = f;
	}
}
