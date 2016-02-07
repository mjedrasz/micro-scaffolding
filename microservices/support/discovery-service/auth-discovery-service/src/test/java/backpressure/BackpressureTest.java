package backpressure;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import reactivestreams.commons.processor.SimpleProcessor;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SchedulerGroup;
import reactor.core.publisher.TopicProcessor;
import reactor.core.queue.QueueSupplier;
import reactor.core.util.PlatformDependent;
import reactor.rx.Stream;
import reactor.rx.StreamSource;

public class BackpressureTest {

	@Test
	public void test() throws InterruptedException {
		System.out.println("ok");
		backpressure2();
	}

	void backpressure() {
		EmitterProcessor<String> producer = EmitterProcessor.create();

		SchedulerGroup asyncGroup = SchedulerGroup.async("async", 2048, 4, Throwable::printStackTrace, null, false);
		Stream.range(0, 10).doOnNext(e -> {
			System.out.println("f: " + e);
		}).dispatchOn(asyncGroup).capacity(2).consume(e -> {
			System.out.println(e);
		});

	}

	void backpressure3() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(10);
		SchedulerGroup asyncGroup = SchedulerGroup.async("async1", 2048, 4, Throwable::printStackTrace, null, false);
		Stream<Long> interval = Stream.interval(1, TimeUnit.SECONDS);
		StreamSource<Long, Long> dis = new StreamSource<>(Flux.dispatchOn(interval, asyncGroup, true,
				PlatformDependent.SMALL_BUFFER_SIZE, QueueSupplier.<Long> small()));
		dis
		// .dispatchOn(asyncGroup)
		// .capacity(2)
		// .doOnNext(System.out::println)
		.subscribe(new Subscriber<Long>() {

			Subscription s;

			@Override
			public void onSubscribe(Subscription s) {
				System.out.println(s.getClass());
				this.s = s;
				s.request(10000);

			}

			@Override
			public void onNext(Long t) {
				System.out.println(t);
				// s.request(1);
				try {
					Thread.sleep(2000);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				latch.countDown();

			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onComplete() {
				System.out.println("comlet");
				// TODO Auto-generated method stub

			}
		});

		latch.await(10, TimeUnit.SECONDS);
	}

	void backpressure2() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(20);
		SchedulerGroup asyncGroup = SchedulerGroup.async("async1", 2048, 4, Throwable::printStackTrace, null, false);
		SchedulerGroup asyncGroup2 = SchedulerGroup.async("async2", 2048, 4, Throwable::printStackTrace, null, false);
		SimpleProcessor<Integer> sp = new SimpleProcessor<>();

		Stream<Integer> range = Stream.range(1, 100);
		Stream.from(sp).dispatchOn(asyncGroup2).log("receiver").
		capacity(2).consume(r -> {
			System.out.println(r);
			latch.countDown();
			try {
				Thread.sleep(200);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		// P
		// producer.doOnNext(i -> { System.out.println("p:" + i); });
		new Thread(new Runnable() {
			public void run() {
				range.dispatchOn(asyncGroup).log("producer").consume(i -> {
					sp.onNext(i);
					try {
						Thread.sleep(100);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					// System.out.println("ab" + i);

				});
			}
		}).start();
		sp.doOnRequest(lc -> {  System.out.println(lc); });
		// sp.onComplete();
		latch.await(10, TimeUnit.SECONDS);

	}
}
