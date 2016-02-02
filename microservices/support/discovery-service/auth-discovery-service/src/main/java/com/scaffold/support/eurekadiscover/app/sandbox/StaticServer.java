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


	public void reactor() throws InterruptedException {
		final int port = 1234;// SocketUtils.findAvailableTcpPort();

		final CountDownLatch latch = new CountDownLatch(1);
		ReactorTcpClient<Pojo, Pojo> client = NetStreams.tcpClient(s -> s.connect("localhost", 1234)
				.preprocessor(CodecPreprocessor.json(Pojo.class)));

		final ReactorTcpServer<Pojo, Pojo> server = NetStreams.tcpServer(s -> s.listen("localhost", port).preprocessor(
				CodecPreprocessor.json(Pojo.class)));

		Broadcaster<Pojo> broadcaster = Broadcaster.create();
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
				for (int i = 0; i < 10; i++) {
					broadcaster.onNext(new Pojo(""+i));
				}
			}
			System.out.println("RECEIVED:" + p.getName());
		}).map(i -> new Pojo(i.getName().toUpperCase()))

		.capacity(1l) // auto-flush every 5 elements

		)

		);
		client.start(ch -> {
			System.out.println("Handling");
			broadcaster.consume(po -> {
				System.out.println(po);
				Stream.from(ch.writeWith(Stream.just(po))).consume();
			});
			return Stream.never();
		}).doOnTerminate((i, t) -> {
			System.out.println("ter" + i + "," + t);
		}).doOnSubscribe(s -> {
			System.out.println("subscrie" + s);
		}).doOnSuccess(s -> {
			System.out.println("Success end");
		}).subscribe();
		System.out.println("End");
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class Pojo {
		private String name;

	}
}