package org.ady.game.model.item;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.ady.game.model.item.impl.DegradableItem;
import org.ady.game.model.item.impl.DragonfireShield;
import org.ady.game.model.item.impl.RunicStaff;
import org.ady.logging.Logger;
import org.ady.utils.NumberUtil;
import org.ady.utils.ReflectionUtil;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
	@JsonSubTypes.Type(value = DegradableItem.class), 
	@JsonSubTypes.Type(value = DragonfireShield.class),
	@JsonSubTypes.Type(value = RunicStaff.class)
})
public abstract class ItemListener extends Item {
	
	@JsonProperty
	@Getter
	@Setter
	private long UID;
	
	public ItemListener(int id, int amount) {
		super(id, amount);
		UID = NumberUtil.grabRandomLong();
	}

	private static final Logger logger = Logger.getLogger(ItemListener.class.getSimpleName());
	
	private static final HashMap<Integer, Class<? extends Item>> listeners = new HashMap<Integer, Class<? extends Item>>();
	
	public static void putListener(int id, Class<? extends Item> clazz) {
		listeners.put(id, clazz);
	}
	
	public static void populateListeners() {
		ReflectionUtil.reflectDirectory("bin/org/ady/game/model/item/impl", clazz -> {
			ItemListener listener = (ItemListener) clazz;
			listener.populate();
		});
	}
	
	public static final Item allocate(int id, int amount) {
		final Class<? extends Item> item = listeners.get(id);
		if (item == null) {
			return new Item(id, amount);
		}
		else {
			try {
				for (Constructor<?> c : item.getConstructors()) { 
					Class<?>[] params = c.getParameterTypes();
					if (params.length == 0) { //spoof constructor for reflection
						continue;
					}
					else {
						return item.getConstructor(params).newInstance(id, amount);
					}
				}
			} catch (Exception e) {
				logger.handleException(e);
			}
		}
		return new Item(id, amount);
	}
	
	/**
	 * Populate the listeners map.
	 */
	public abstract void populate();
	

}
