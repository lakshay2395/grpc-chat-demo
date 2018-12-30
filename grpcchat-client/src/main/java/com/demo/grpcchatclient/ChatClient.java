package com.demo.grpcchatclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.demo.grpcchat.proto.ChatGrpc;
import com.demo.grpcchat.proto.ChatOuterClass.Message;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

@Component("chatClient")
public class ChatClient {
	
	ChatGrpc.ChatStub stub;
	
	@PostConstruct
	public void init() {
		try {
			ManagedChannel managedChannel = ManagedChannelBuilder
					.forAddress("localhost", 6565).usePlaintext().build();
			stub = ChatGrpc.newStub(managedChannel);
			StreamObserver<Message> request = stub.chatCall(new StreamObserver<Message>() {
	
				@Override
				public void onNext(Message value) {
					System.out.println("New Message At"+value.getTime()+" : "+value.getContent());
				}
	
				@Override
				public void onError(Throwable t) {
					System.err.println("Error occurred : "+t.getMessage());
				}
	
				@Override
				public void onCompleted() {
					System.out.println("Chat Closed From server end");
				}
			});
			
			while(true) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				System.out.println("Type your message : ");
					String content = reader.readLine();
					if(content.equalsIgnoreCase("EXIT"))
						request.onCompleted();
					else
						request.onNext(Message.newBuilder().setContent(content)
								.setTime(com.google.protobuf.Timestamp.newBuilder().setSeconds(System.currentTimeMillis()).build()).build());
				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
