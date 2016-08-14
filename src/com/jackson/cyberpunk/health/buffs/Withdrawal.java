package com.jackson.cyberpunk.health.buffs;

import com.jackson.cyberpunk.item.Drug;
import com.jackson.cyberpunk.mob.Mob;

public class Withdrawal extends Buff<Mob> {
	/**
	 * Measures withdrawal on current drug, from 0 to 1f
	 */
	private float withdrawalValue = 0;
	private float addiction = 0;
	private float speed = 0;
	
	private int timeSinceLastFlying = 0;
	
	private Drug drug;
	
	public Withdrawal(Mob holder, Drug drug) {
		super(holder, "withdrawal",
			new String[] {
					"ломка по " + drug.getDescription() 
						+ "\nСнижена точность и шанс успеха действий\n"
						+ "\nДебаф к настроению",
					"сильная ломка по " + drug.getDescription()
						+ "\nСнижена точность и шанс успеха действий\n"
						+ "\nБольшой дебаф к настроению"});
		this.drug = drug;
	}
	
	protected void processDrugReception() {
		
	}
	
	public Drug getDrug() {
		return drug;
	}
	
	@Override
	protected int checkLevel() {
		
		return 0;
	}

	@Override
	protected void update2() {
		level = checkLevel();
	}

	@Override
	public boolean equals(Object obj) {
		//return super.equals(obj) && drug == ((WithdrawalBuff) obj).drug;
		return false;
	}
}
