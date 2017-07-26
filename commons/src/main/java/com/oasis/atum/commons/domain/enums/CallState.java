package com.oasis.atum.commons.domain.enums;

/**
 * 通话状态
 */
public enum CallState
{
	dealing("已接"), notDeal("振铃未接听"), leak("ivr放弃"), queueLeak("排队放弃"), blackList("黑名单"), voicemail("留言");

	public final String name;

	CallState(final String name)
	{
		this.name = name;
	}
}
