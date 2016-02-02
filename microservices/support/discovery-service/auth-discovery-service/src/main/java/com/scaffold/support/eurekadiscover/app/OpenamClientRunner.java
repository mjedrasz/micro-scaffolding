package com.scaffold.support.eurekadiscover.app;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.reactivestreams.Processor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.SocketUtils;

import reactor.io.buffer.Buffer;
import reactor.io.codec.json.JsonCodec;
import reactor.io.net.ReactiveChannelHandler;
import reactor.io.net.preprocessor.CodecPreprocessor;
import reactor.rx.Broadcaster;
import reactor.rx.Stream;
import reactor.rx.net.ChannelStream;
import reactor.rx.net.NetStreams;
import reactor.rx.net.tcp.ReactorTcpClient;
import reactor.rx.net.tcp.ReactorTcpServer;

@Component
public class OpenamClientRunner implements CommandLineRunner {

	@Override
	public void run(String... strings) throws Exception {
		System.out.println("i've run tii");
//		tcpServerHandlesJsonPojosOverSsl();
	}

	void x() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(10);

		int port = SocketUtils.findAvailableTcpPort();

		final ReactorTcpServer<Pojo, Pojo> server = NetStreams.tcpServer(s -> s.listen("localhost", port).preprocessor(
				CodecPreprocessor.json(Pojo.class)));

		final ReactorTcpClient<Pojo, Pojo> client = NetStreams.tcpClient(s -> s.connect("localhost", port)
				.preprocessor(CodecPreprocessor.json(Pojo.class)));

		final JsonCodec<Pojo, Pojo> codec = new JsonCodec<Pojo, Pojo>(Pojo.class);

		// the client/server are prepared

		server.start(input ->

		// for each connection echo any incoming data

				// return the write confirm publisher from writeWith

				// >>> close when the write confirm completed

				input.writeWith(

				// read incoming data

				input

				.log("serve")
				
				.doOnNext(p -> {
					System.out.println(p.getName());
				})

				.capacity(5l) // auto-flush every 5 elements

				)

		).get();

		client.start(input -> {

			// read 10 replies and close

				// input
				//
				// .take(10)
				//
				//
				// .log("receive")
				//
				// .consume( data -> latch.countDown() );

				// write data

				input.writeWith(

				ddd()

				);

				// keep-alive, until 10 data have been read

				return Stream.never();

			}).get();

		latch.await(10, TimeUnit.SECONDS);

		client.shutdown().get();

		server.shutdown().get();
	}

	public void tcpServerHandlesJsonPojosOverSsl() throws InterruptedException {
		final int port = SocketUtils.findAvailableTcpPort();

		final CountDownLatch latch = new CountDownLatch(1);
		final ReactorTcpClient<Pojo, Pojo> client = NetStreams.tcpClient(s -> s.connect("localhost", port)
				.preprocessor(CodecPreprocessor.json(Pojo.class)));

		final ReactorTcpServer<Pojo, Pojo> server = NetStreams.tcpServer(s -> s.listen("localhost", port).preprocessor(
				CodecPreprocessor.json(Pojo.class)));

		server.start(channel -> {
			channel.log("conn").consume(data -> {
				System.out.println(data.getName());
				latch.countDown();
			});
			return Stream.never();
		});

		Broadcaster<Pojo> b = Broadcaster.create();
		final Processor<Pojo, Pojo> keyboardStream = null;//RingBufferProcessor.create();
//		b
//		  // produce demand
//		  .consume(s -> System.out.println(Thread.currentThread() + ": " + s.getName()));
		// Sink values into this Broadcaster
		client.start(ch -> {
			
			return ch.writeWith(keyboardStream);
		}
				).get();
		keyboardStream.onNext(new Pojo("fds"));
		latch.await(10, TimeUnit.SECONDS);
		client.shutdown();
		server.shutdown();
	}

	ChannelStream<Pojo, Pojo> stre;
	/**
	 * @param stream
	 * @return
	 */
	private ReactiveChannelHandler<Pojo, Pojo, ChannelStream<Pojo, Pojo>> abc(Stream<Pojo> stream) {
		
		return ch -> { 
			chStream = ch;
			return ch.writeWith(stream

		);};
	}
	
	private ChannelStream<Pojo, Pojo> chStream;

	/**
	 * @return
	 */
	private Stream<Pojo> ddd() {
		return Stream.range(1, 10)

		.map(it -> {
			System.out.println(it);
			return new Pojo("test" + it);
		})

		.log("send");
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class Pojo {
		private String name;

	}
	
	private class DataWriter implements Runnable {
		private final int port;

		private DataWriter(int port) {
			this.port = port;
		}

		@Override
		public void run() {
			try {
				java.nio.channels.SocketChannel ch = java.nio.channels.SocketChannel.open(new InetSocketAddress(port));
				for (int i = 0; i < 100; i++) {
					ch.write(Buffer.wrap("Hello World!\n").byteBuffer());
				}
				ch.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
    public static void main(String[] args) throws InterruptedException{
        // environment initialization

        JsonCodec<A, A> codec = new JsonCodec<A, A>(A.class);


        final ReactorTcpServer<Pojo, Pojo> server = NetStreams.tcpServer(s -> s.listen("localhost", 6777).preprocessor(
				CodecPreprocessor.json(Pojo.class)));

		server.start(channel -> {
			channel.log("conn").consume(data -> {
				System.out.println(data.getName());
			});
			return Stream.never();
		});

       

        // TCP client
		final ReactorTcpClient<Pojo, Pojo> client = NetStreams.tcpClient(s -> s.connect("localhost", 6777)
				.preprocessor(CodecPreprocessor.json(Pojo.class)));
		

     

        Thread.sleep(Long.MAX_VALUE);
    }
}

class A {
    private int id;

    A() {}
    A(int id) {
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}