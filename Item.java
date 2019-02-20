package org.ady.game.model.item;

import org.ady.game.cache.definitions.ItemDefinition;
import org.ady.game.constants.GameConstants;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
	@JsonSubTypes.Type(value = ItemListener.class)
})
public class Item {
	
	@Getter
	@Setter
	private int id;
	
	@Getter
	@Setter
	private int amount;
	
	public Item(@JsonProperty("id") int id, @JsonProperty("amount") int amount) {
		//@JsonIgnore doesn't work on constructors, will need an empty spoof contructor if not annotating each param
		//as a json property on the constructor builder, otherwise 
		//com.fasterxml.jackson.databind.JsonMappingException: No suitable constructor found for type 
		this.id = id;
		this.amount = amount;
	}

	public ItemDefinition getDefinition() {
		return GameConstants.CACHE.getItemDefinition(id);
	}
	
	@Override
	public Item clone() {
		Item item = ItemListener.allocate(id, amount);
		return item;
	}
	
	@Override
	public String toString() {
		return "[name =" + getDefinition().getName() + ", id =" + id + ", amount=" + amount + "]";
	}
	
	public String getExamine() {
		return toString();
	}
	
}
