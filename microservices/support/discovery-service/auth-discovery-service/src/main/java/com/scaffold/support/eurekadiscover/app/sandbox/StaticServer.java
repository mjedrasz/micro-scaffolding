package com.scaffold.support.eurekadiscover.app.sandbox;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.Pump;
import io.vertx.ext.reactivestreams.ReactiveReadStream;
import io.vertx.ext.reactivestreams.ReactiveWriteStream;

import java.util.concurrent.CountDownLatch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.springframework.stereotype.Component;

import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Mono;
import reactor.core.subscriber.ConsumerSubscriber;
import reactor.io.net.preprocessor.CodecPreprocessor;
import reactor.rx.Broadcaster;
import reactor.rx.Stream;
import reactor.rx.net.ChannelStream;
import reactor.rx.net.NetStreams;
import reactor.rx.net.tcp.ReactorTcpClient;
import reactor.rx.net.tcp.ReactorTcpServer;

@Component
public class StaticServer extends AbstractVerticle {

	@Override
	public void start() throws Exception {

		// vertx();
		reactor();
		System.out.println("Echo server is now listening");

	}

	/**
	 * 
	 */
	private void vertx() {
		final ReactiveReadStream<Buffer> rrs = ReactiveReadStream.readStream();
		vertx.createNetServer().connectHandler(sock -> {
			ReactiveWriteStream<Buffer> rws = ReactiveWriteStream.writeStream(vertx);
			EmitterProcessor<Buffer> create = EmitterProcessor.create();
			create.subscribe(rrs);
			ConsumerSubscriber<Buffer> afa = new ConsumerSubscriber<Buffer>(t -> {
				System.out.println(t);
				create.onNext(t.appendString("really"));
			}, null, null);
			rws.subscribe(afa);
			create.start();
			// Create a pump
				Pump.pump(sock, rws).start();
				Pump.pump(rrs, sock).start();

			}).listen(1234);
	}

	ReactorTcpClient<Pojo, Pojo> client;
	ChannelStream<Pojo, Pojo> ch2 = null;
	public void reactor() throws InterruptedException {
		final int port = 1234;// SocketUtils.findAvailableTcpPort();

		final CountDownLatch latch = new CountDownLatch(1);
		client = NetStreams.tcpClient(s -> s.connect("localhost", 1234)
				.preprocessor(CodecPreprocessor.json(Pojo.class)));

		final ReactorTcpServer<Pojo, Pojo> server = NetStreams.tcpServer(s -> s.listen("localhost", port).preprocessor(
				CodecPreprocessor.json(Pojo.class)));
		
		server.start(input ->

		// for each connection echo any incoming data

		// return the write confirm publisher from writeWith

		// >>> close when the write confirm completed

		input.writeWith(

		// read incoming data

		input

		.log("serve")

		.doOnNext(p -> {
			if (p.getName().equals("working")) {
				System.out.println("tutaj");
			}
			System.out.println("RECEIVED:"+p.getName());
		}).map(i -> new Pojo(i.getName().toUpperCase()))

		.capacity(10l) // auto-flush every 5 elements

		)

		);
		
		Broadcaster<Pojo> b = Broadcaster.create();
//		Broadcaster<Pojo> sink = Broadcaster.create();
//		b.doOnNext(po -> {
//			System.out.println(po);
//		}).consume();
		EmitterProcessor<Pojo> create = EmitterProcessor.create();
//		 create.subscribe(b);
		 create.start();
//		 Publisher<Pojo> p = create;
		 
//		 create.onNext(new Pojo("to jest to11"));
//		 b.onNext(new Pojo("fsdfadfa"));
		 
		client.start(ch -> {
			b.consume(po -> {
				System.out.println(po);
//				ch.writeWith(Stream.just(po));
				Stream.from(ch.writeWith(Stream.just(po))).consume();
			});
//			ch2 = ch;
//			ch2.subscribe(create);
//			Stream.from(ch2.writeWith(create)).consume();
			return Stream.never();
		}).doOnTerminate((i, t) -> {
			System.out.println("ter"+i + ","+ t);
//			Stream.from(ch2.writeWith(Stream.just(new Pojo("suu")))).consume();
		})
		.doOnSubscribe(s -> {
			System.out.println("subscrie"+s);
//			Stream.from(ch2.writeWith(Stream.just(new Pojo("suu")))).consume();
		})
		.doOnSuccess(s -> {
			System.out.println("success"+s);
//			Stream.from(ch2.writeWith(Stream.just(new Pojo("sss")))).consume();
		}).subscribe();
		
//		b.startWith(new Pojo("fsad"));
//		Thread.sleep(3000);
//		Stream.from(ch2.writeWith(Stream.just(new Pojo("dfasfa")))).consume();
//		Stream.from(ch2.writeWith(Stream.just(new Pojo("yyyy")))).consume();
//		Stream.from(ch2.writeWith(Stream.empty())).consume();
//		Stream.from(ch2.writeWith(create)).consume();
//		ch2.subscribe(create);
//		ch2.writeWith(b);
//		ch2.writeWith(create);
//		client.start(ch -> {
//			
//			System.out.println(ch);
//			return ch.writeWith(Stream.just(new Pojo("3332")));
//		}).subscribe();
//		sink.onNext(new Pojo("aaaa"));
//		create.onNext(new Pojo("to jest to"));
		create.doOnNext(po -> {
			System.out.println(po);
		});
//		b.consume(po -> {
//			System.out.println("B:"+po);
//		});
		create.onNext(new Pojo("00000"));
		create.onNext(new Pojo("1111111"));
		 b.onNext(new Pojo("22222"));
		 b.onNext(new Pojo("33333"));
		 create.onNext(new Pojo("44444"));
		// broadcast.onNext(new Pojo("at last"));
		//
		//
		// client.shutdown();
//		 latch.await(30, TimeUnit.SECONDS);
		// server.shutdown();
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class Pojo {
		private String name;

	}
}