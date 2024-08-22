### Notes Fabric Tutorials

#### How to run project with certain username?
To run the project with a certain username, you have to edit the run configuration in the IDE arguments. 
```bash
--username USERNAME
```

#### To log something in the console
```java
    LOGGER.info("Merchant Screen is opened");
```

#### To send message inside the game
```java
    player.sendMessage(new LiteralText("Hello World!"), false);
```

### Types of screens in fabric
- **ContainerScreen**: A screen that has a container.
- **TradeScreen**: A screen that has a trade.

### How to send message in the  chat
```java
    player.sendMessage(new LiteralText("Hello World!"), false);
```

### How to make your player send message
```java
    MinecraftClient.getInstance().player.networkHandler.sendChatMessage("Hello World!");
```


### How to track messages in the chat
```java
    // First add this line in the onInitialize methodh
    ClientSendMessageEvents.ALLOW_CHAT.register(this::onChatMessage);


// Then add this method in the class
private boolean onChatMessage(String message) {
    if (message.startsWith("!ran")) {
        // Generate a random number
        Random random = new Random();
        int randomNumber = random.nextInt(100) + 1; // Generates a number between 1 and 100

        // Send the random number as a chat message
        if (client.player != null) {
            client.player.sendMessage(Text.literal("Random Number: " + randomNumber), false);
        }

        // Returning false to prevent the original message from being sent
        return false;
    }

    // Allow normal message processing if it's not the "!ran" command
    return true;
}

```