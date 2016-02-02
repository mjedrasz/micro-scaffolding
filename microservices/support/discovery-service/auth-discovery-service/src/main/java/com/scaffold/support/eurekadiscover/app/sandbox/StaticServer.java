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

import org.springframework.stereotype.Component;

import reactor.core.publisher.EmitterProcessor;
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

	public void reactor() throws InterruptedException {
		final int port = 1234;// SocketUtils.findAvailableTcpPort();

		final CountDownLatch latch = new CountDownLatch(1);
		client = NetStreams.tcpClient(s -> s.connect("localhost", port)
				.preprocessor(CodecPreprocessor.json(Pojo.class)));

		final ReactorTcpServer<Pojo, Pojo> server = NetStreams.tcpServer(s -> s.listen("localhost", port).preprocessor(
				CodecPreprocessor.json(Pojo.class)));
		EmitterProcessor<Pojo> create = EmitterProcessor.create();
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
				create.onNext(new Pojo(Math.random() + "bbbb"));
				System.out.println("tutaj");
			}
			System.out.println("srv" + p.getName());
		}).map(i -> new Pojo(i.getName().toUpperCase()))

		.capacity(1l) // auto-flush every 5 elements

		)

		);
		
		Broadcaster<Pojo> b = Broadcaster.create();
		// create.subscribe(b);
		 create.start();

		client.start(ch -> 
//			ch.subscribe(create);
//			ch.take(1).consume(d -> {System.out.println("abc:" + d);});
//			return ch.writeWith(
				ch.writeWith(Stream.just(new Pojo("fdas"))))
//				Stream<Pojo> broadcast = ch.broadcast();
//			}));
		.get();
//
//		b.onNext(new Pojo("aaaa"));
//		create.onNext(new Pojo("bbbb"));
		// b.onNext(new Pojo("fsdfadfa"));
		// broadcast.onNext(new Pojo("at last"));
		//
		//
		// client.shutdown();
		// latch.await(10, TimeUnit.SECONDS);
		// server.shutdown();
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class Pojo {
		private String name;

	}
}