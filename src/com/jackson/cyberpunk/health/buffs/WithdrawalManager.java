package com.jackson.cyberpunk.health.buffs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jackson.cyberpunk.health.Effect;
import com.jackson.cyberpunk.item.Drug;

public class WithdrawalManager extends Buff {
	private List<Withdrawal> withdrawals = new ArrayList<>();

	public WithdrawalManager() {
		super(null, null, null);
	}

	public void receptDrug(Drug d) {
		withdrawals.forEach(w -> receptDrug(d));
	}
	
	@Override
	protected int checkLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void update2() {

	}
}
