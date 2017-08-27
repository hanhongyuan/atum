package com.oasis.atum.base.infrastructure.util;

import lombok.Synchronized;
import lombok.val;

import java.util.Optional;
import java.util.stream.Stream;

import static io.vavr.API.Option;

/**
 * 分布式全局ID生成器
 * tweeter的snowflake 移植到Java:
 * (a) id构成: 42位的时间前缀 + 10位的节点标识 + 12位的sequence避免并发的数字(12位不够用时强制得到新的时间前缀)
 * 注意这里进行了小改动: snowkflake是5位的datacenter加5位的机器id; 这里变成使用10位的机器id
 * (b) 对系统时间的依赖性非常强，需关闭ntp的时间同步功能。当检测到ntp时间调整后，将会拒绝分配id
 * <p>
 * Created by ryze on 2017/5/11.
 */
public final class IdWorker
{
	private final long workerId;
	// 时间起始标记点，作为基准，一般取系统的最近时间
	private final long epoch              = 1403854494756L;
	// 机器标识位数
	private final long workerIdBits       = 10L;
	// 机器ID最大值: 1023
	private final long maxWorkerId        = -1L ^ (-1L << workerIdBits);
	// 毫秒内自增位
	private final long sequenceBits       = 12L;
	//并发控制
	private       long sequence           = 0L;
	// 12
	private final long workerIdShift      = sequenceBits;
	// 22
	private final long timestampLeftShift = sequenceBits + workerIdBits;
	// 4095,111111111111,12位
	private final long sequenceMask       = -1L ^ (-1L << workerIdBits);
	private       long lastTimestamp      = -1L;

	private IdWorker(final long workerId)
	{
		if (workerId > maxWorkerId || workerId < 0)
		{
			throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
		}
		this.workerId = workerId;
	}

	private static IdWorker flowIdWorker = new IdWorker(1);

	public static IdWorker getFlowIdWorkerInstance()
	{
		return flowIdWorker;
	}

	/**
	 * 等待下一个毫秒的到来, 保证返回的毫秒数在参数lastTimestamp之后
	 */
	private long blockNextMillis(final long lastTimestamp)
	{
		val timestamp = System.currentTimeMillis();
		return timestamp <= lastTimestamp ? System.currentTimeMillis() : timestamp;
	}

	@Synchronized
	public long nextId()
	{
		return Option(System.currentTimeMillis())
						 .map(t ->
						 {
							 //当前时间戳小于上一次Id生成的时间戳,说明系统时间回退过,这个时候应当抛出异常
							 if (t < lastTimestamp) throw new RuntimeException("系统时间不正常,拒绝为" + (lastTimestamp - t) + "毫秒生成id");
							 if (t.equals(lastTimestamp))
							 {
								 sequence = (sequence + 1) & sequenceMask;
								 //毫秒内序列溢出
								 if (sequence == 0L) blockNextMillis(lastTimestamp);
							 }
							 //时间戳改变,毫秒内序列重置
							 else sequence = 0L;
							 //上次生成ID的时间截
							 lastTimestamp = t;
							 //移位并通过或运算拼到一起组成64位的ID
							 return ((t - epoch) << timestampLeftShift) | (workerId << workerIdShift) | sequence;
						 }).get();
	}

	public String nextSID()
	{
		return nextId() + "";
	}

	public static void main(String[] args) throws Exception
	{

		IdWorker idWorker = IdWorker.getFlowIdWorkerInstance();
		Stream.iterate(idWorker.nextId(), l -> idWorker.nextId()).limit(100000).forEach(System.out::println);
	}
}
