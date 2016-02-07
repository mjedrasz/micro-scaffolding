package com.scaffold.support.eurekadiscover.app.sandbox;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.xml.XmlFrameDecoder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.Pump;
import io.vertx.ext.reactivestreams.ReactiveReadStream;
import io.vertx.ext.reactivestreams.ReactiveWriteStream;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.stereotype.Component;

import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.SchedulerGroup;
import reactor.core.publisher.TopicProcessor;
import reactor.core.subscriber.ConsumerSubscriber;
import reactor.io.codec.BufferCodec;
import reactor.io.net.impl.netty.NettyClientSocketOptions;
import reactor.io.net.impl.netty.NettyServerSocketOptions;
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
		// reactor();
		reactorxml();
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

		ReactorTcpClient<Pojo, Pojo> client = NetStreams.tcpClient(s -> s.connect("localhost", 4321).preprocessor(
				CodecPreprocessor.json(Pojo.class)));
		// .options(new NettyClientSocketOptions()
		// .pipelineConfigurer(pipeline -> pipeline.addLast(new
		// HttpClientCodec()))));

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
				for (int i = 0; i < 1; i++) {
					broadcaster.onNext(new Pojo("" + Math.random()));
				}
			}
			System.out.println("RECEIVED:" + p.getName());
		}).map(i -> new Pojo(i.getName().toUpperCase()))

		.capacity(1l) // auto-flush every 5 elements

		)

		);
		client.start(ch -> {
			System.out.println("Handling");
			// ch.take(3).consume(p -> {
			// System.out.println("crec:" + p);
			//
			// });
				ch.subscribe(new Subscriber<Pojo>() {
					Subscription s;

					@Override
					public void onSubscribe(Subscription s) {
						// TODO Auto-generated method stub
						this.s = s;
						System.out.println("onSubscribe");
						this.s.request(1);

					}

					@Override
					public void onNext(Pojo t) {
						System.out.println("onNext" + t);
						// s.request(1);
						// TODO Auto-generated method stub

					}

					@Override
					public void onError(Throwable t) {
						System.out.println("onError");
						// TODO Auto-generated method stub

					}

					@Override
					public void onComplete() {
						System.out.println("onComplete");
						// TODO Auto-generated method stub

					}
				});
				broadcaster.consume(po -> {
					System.out.println(po);
					ch.writeWith(Stream.just(po).log("send").map(j -> new Pojo(j.getName().toUpperCase()))).subscribe();
				});
				return Stream.never();
			}).doOnTerminate((i, t) -> {
			System.out.println("ter" + i + "," + t);
		}).doOnSubscribe(s -> {
			System.out.println("subscrie" + s);
		}).doOnSuccess(s -> {
			System.out.println("Success end");
		}).subscribe();
		;

		broadcaster.onNext(new Pojo("first"));
		System.out.println("End1");
		broadcaster.onNext(new Pojo("second"));
		System.out.println("End2");

		// ReactorTcpClient<reactor.io.buffer.Buffer, reactor.io.buffer.Buffer>
		// client2 = NetStreams.tcpClient(s -> s.connect("localhost", 1234));
		// client2.start(c ->
		// c.writeWith(Stream.just(reactor.io.buffer.Buffer.wrap("{\"name\""),
		// reactor.io.buffer.Buffer.wrap(":\"ok\"}")))).get();

	}

	public void reactorxml() throws InterruptedException {
		final int port = 2345;// SocketUtils.findAvailableTcpPort();

		ReactorTcpClient<Pojo, Pojo> client = NetStreams.tcpClient(s -> s.connect("localhost", port).options(
				new NettyClientSocketOptions().pipelineConfigurer(pipeline -> {
					pipeline.addLast(new XmlFrameDecoder(10000));
					pipeline.addLast(new XmlToPojoDecoder());
				})));

		final ReactorTcpServer<Pojo, Pojo> server = NetStreams.tcpServer(s -> s.listen("localhost", port).options(
				new NettyServerSocketOptions().pipelineConfigurer(pipeline -> {
					pipeline.addLast("decoder", new XmlFrameDecoder(10000));
//					.addLast(new XmlToPojoDecoder());
//					pipeline.addLast("encoder", new XmlToPojoEncoder());
				})).preprocessor(CodecPreprocessor.from(new PojoCodec(), new PojoCodec())
				));

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
			// if (p.getName().equals("working")) {
			// System.out.println("tutaj");
			// for (int i = 0; i < 1; i++) {
			// broadcaster.onNext(new Pojo("" + Math.random()));
			// }
			// }
				System.out.println("RECEIVED:" + p);
			}).map(e -> e)

		.capacity(1l) // auto-flush every 5 elements

		)

		);
		// client.start(ch -> {
		// System.out.println("Handling");
		// // ch.take(3).consume(p -> {
		// // System.out.println("crec:" + p);
		// //
		// // });
		//
		// broadcaster.consume(po -> {
		// System.out.println(po);
		// ch.writeWith(Stream.just(po).log("send").map(j -> new
		// Pojo(j.getName().toUpperCase()))).subscribe();
		// });
		// return Stream.never();
		// }).subscribe();;

		// ReactorTcpClient<reactor.io.buffer.Buffer, reactor.io.buffer.Buffer>
		// client2 = NetStreams.tcpClient(s -> s.connect("localhost", 1234));
		// client2.start(c ->
		// c.writeWith(Stream.just(reactor.io.buffer.Buffer.wrap("{\"name\""),
		// reactor.io.buffer.Buffer.wrap(":\"ok\"}")))).get();

	}

	private static class XmlToPojoDecoder extends ByteToMessageDecoder {
		@Override
		protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
			System.out.println("decoding");
			ByteArrayInputStream bais = new ByteArrayInputStream(new byte[msg.readableBytes()]);
			msg.writeBytes(bais, msg.readableBytes());
			System.out.println(bais.toString());
//			msg.forEachByte(i -> {System.out.print(i); return false;});
//			msg.getByteBuf().forEachByte(value -> {
//				System.out.println(value);
//			});

//			System.out.println("WTF:" + new String(msg.array()));
//			System.out.println("msg:" + new String(msg.asString()));
			out.add(new Pojo("fs"));
		}
	}
	private static class XmlToPojoEncoder extends MessageToByteEncoder<Pojo> {
		@Override
		protected void encode(ChannelHandlerContext ctx, Pojo msg, ByteBuf out) throws Exception {
			System.out.println("msg:" + msg.getName());
			out.writeBytes(msg.getName().getBytes());
		}
	}
//	private static class XmlToPojoEncoder extends MessageToByteEncoder<Pojo> {
//		@Override
//		protected void encode(ChannelHandlerContext ctx, Pojo msg, ByteBuf out) throws Exception {
//			System.out.println("msg:" + msg.getName());
//			out.writeBytes(msg.getName().getBytes());
//		}
//	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class Pojo {
		private String name;

	}

	private static class PojoCodec extends BufferCodec<Pojo, Pojo> {

		@Override
		protected int canDecodeNext(reactor.io.buffer.Buffer buffer, Object context) {
			return buffer.remaining() > 0 ? buffer.limit() : -1;
		}


		@Override
		public reactor.io.buffer.Buffer apply(Pojo t) {
			return reactor.io.buffer.Buffer.wrap(t.getName().getBytes());
		}

		@Override
		protected Pojo decodeNext(reactor.io.buffer.Buffer buffer, Object context) {
			byte[] bytes = buffer.asBytes();
			buffer.skip(bytes.length);
			return new Pojo(new String(bytes));
		}

	}
	
	void backpressure() {
		TopicProcessor<Long> t = TopicProcessor.create();
		EmitterProcessor<String> producer = EmitterProcessor.create();
		ExecutorService e;
		SchedulerGroup asyncGroup = SchedulerGroup.async("async", 2048, 4, Throwable::printStackTrace, null, false);
		Stream.from(t).dispatchOn(asyncGroup).capacity(1).consume(System.out::println);
		t.onNext(1l);
		// producer.onNext("111");
	}
}