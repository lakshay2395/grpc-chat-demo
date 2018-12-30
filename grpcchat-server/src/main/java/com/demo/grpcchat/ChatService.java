package com.demo.grpcchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.lognet.springboot.grpc.GRpcService;
import com.demo.grpcchat.proto.ChatGrpc;
import com.demo.grpcchat.proto.ChatOuterClass.Message;

import io.grpc.stub.StreamObserver;


@GRpcService
public class ChatService extends ChatGrpc.ChatImplBase{
	
	@Override
	public StreamObserver<Message> chatCall(final StreamObserver<Message> response){
		return new StreamObserver<Message>() {

			@Override
			public void onNext(Message value) {
				System.out.println("New Message At"+value.getTime()+" : "+value.getContent());
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				System.out.println("Type your message : ");
				try {
					String content = reader.readLine();
					if(content.equalsIgnoreCase("EXIT"))
						response.onCompleted();
					else
						response.onNext(Message.newBuilder().setContent(content)
								.setTime(com.google.protobuf.Timestamp.newBuilder().setSeconds(System.currentTimeMillis()).build()).build());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable t) {
				System.err.println("Error occurred : "+t.getMessage());
			}

			@Override
			public void onCompleted() {
				response.onCompleted();
			}
			
		};
	}

}
