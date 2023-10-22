package br.com;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws Exception {
        String projectId = "sample-project-pubsub";
        String topicId = "sample-topic";

        doPublishMessages(projectId, topicId);
    }

    private static void doPublishMessages(String projectId, String topicId)
            throws IOException, ExecutionException, InterruptedException {
        TopicName topicName = TopicName.of(projectId, topicId);
        Publisher publisher = null;
        try {
            publisher = Publisher.newBuilder(topicName).build();

            for (int i = 0; i < 10; i++) {
                String message = "Message ".concat(String.valueOf(i));
                ByteString data = ByteString.copyFromUtf8(message);
                PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

                ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
                String messageId = messageIdFuture.get();
                System.out.println("Published message ID: " + messageId);
            }
        } finally {
            if (publisher != null) {
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }
    }
}