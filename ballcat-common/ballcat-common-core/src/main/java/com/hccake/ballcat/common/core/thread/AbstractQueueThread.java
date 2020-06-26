package com.hccake.ballcat.common.core.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 抽象的线程类，主要用于汇聚详情数据 做一些基础的处理后 进行批量插入
 *
 * @author Hccake
 */
@Slf4j
public abstract class AbstractQueueThread<T> extends Thread implements InitializingBean {

	private final BlockingQueue<T> queue = new LinkedBlockingQueue<T>();

	private final static long DEFAULT_BATCH_SIZE = 100;

	private final static long DEFAULT_BATCH_TIMEOUT = 30000;

	public void putObject(T t) {
		try {
			if (t != null) {
				queue.put(t);
			}
		}
		catch (Exception e) {
			log.error("{} putObject error, param: {}", this.getClass().toString(), t, e);
		}
	}

	public long getBatchSize() {
		return DEFAULT_BATCH_SIZE;
	}

	public long getBatchTimeout() {
		return DEFAULT_BATCH_TIMEOUT;
	}

	@Override
	public void run() {

		startLog();

		while (!isInterrupted()) {
			List<T> list = new ArrayList<T>();

			try {
				preProcessor();

				long ts = 0;
				int i = 0;

				while (i < getBatchSize()) {
					T tmp = null;
					try {
						tmp = queue.poll(5000, TimeUnit.MILLISECONDS);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (tmp != null) {
						// 记录第一次添加数据后的时间
						if (i++ == 0) {
							ts = System.currentTimeMillis();
						}
						// 处理
						processor(list, tmp);
					}
					// 有数据 且从第一次插入数据已经过了 设定时间 则 执行插入
					if (list.size() > 0 && System.currentTimeMillis() - ts >= getBatchTimeout()) {
						break;
					}
				}

				save(list);
			}
			catch (Throwable e) {
				errorLog(e, list);
			}
		}
	}

	private void preProcessor() {

	}

	/**
	 * 线程启动时的日志打印
	 */
	public abstract void startLog();

	/**
	 * 错误日志打印
	 * @param e
	 * @param list
	 */
	public abstract void errorLog(Throwable e, List<T> list);

	/**
	 * 数据处理
	 * @param list
	 * @param elem
	 */
	public void processor(List<T> list, T elem) {
		list.add(elem);
	}

	/**
	 * 数据保存
	 * @param list
	 */
	public abstract void save(List<T> list) throws Exception;

	/**
	 * 初始化后启动
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() {
		this.start();
	}

}
