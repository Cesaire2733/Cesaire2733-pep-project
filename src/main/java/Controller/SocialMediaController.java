package Controller;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import DAO.AccountDAO;
import DAO.MessageDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */

     

     private AccountService accountService;
    private MessageService messageService;

    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    public SocialMediaController() {
        this.accountService = new AccountService(new AccountDAO());
        this.messageService = new MessageService(new MessageDAO());
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();

                // Account Endpoints
            app.post("/register", this::registerHandler);
            app.post("/login", this::loginHandler);

            // Message Endpoints
            app.post("/messages", this::createMessageHandler);
            app.get("/messages", this::getAllMessagesHandler);
            app.get("/messages/{id}", this::getMessageByIdHandler); // Updated
            app.delete("/messages/{id}", this::deleteMessageHandler); // Updated
            app.patch("/messages/{id}", this::updateMessageHandler); // Updated
            app.get("/accounts/{id}/messages", this::getMessagesByUserIdHandler); // Updated

            return app;
    }

    // Account Handlers
    private void registerHandler(Context context) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Account account = mapper.readValue(context.body(), Account.class);
            Account createdAccount = accountService.registerAccount(account);
            if (createdAccount != null) {
                context.json(createdAccount);
            } else {
                context.status(400);
            }
        } catch (Exception e) {
            context.status(400);
        }
    }

    private void loginHandler(Context context) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Account account = mapper.readValue(context.body(), Account.class);
            Account loggedInAccount = accountService.loginAccount(account.getUsername(), account.getPassword());
            if (loggedInAccount != null) {
                context.json(loggedInAccount);
            } else {
                context.status(401);
            }
        } catch (Exception e) {
            context.status(400);
        }
    }

    // Message Handlers
    private void createMessageHandler(Context context) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Message message = mapper.readValue(context.body(), Message.class);
            Message createdMessage = messageService.createMessage(message);
            if (createdMessage != null) {
                context.json(createdMessage);
            } else {
                context.status(400);
            }
        } catch (Exception e) {
            context.status(400);
        }
    }

    private void getAllMessagesHandler(Context context) {
        List<Message> messages = messageService.getAllMessages();
        context.json(messages);
    }

    private void getMessageByIdHandler(Context context) {
        int messageId = Integer.parseInt(context.pathParam("id"));
        Message message = messageService.getMessageById(messageId);
        if (message != null) {
            context.json(message);
        } else {
            context.status(200);
            context.result(""); // Return an empty body
        }
    }

    private void deleteMessageHandler(Context context) {
        int messageId = Integer.parseInt(context.pathParam("id"));
        Message deletedMessage = messageService.getMessageById(messageId); // Fetch the message before deletion
        if (deletedMessage != null) {
            boolean isDeleted = messageService.deleteMessage(messageId);
            if (isDeleted) {
                context.json(deletedMessage); // Return the deleted message as JSON
                return;
            }
        }
        context.status(200); // If no message is found, return 200 with an empty body
        context.result("");
    }

    private void updateMessageHandler(Context context) {
        int messageId = Integer.parseInt(context.pathParam("id"));
        ObjectMapper mapper = new ObjectMapper();
        try {
            Message message = mapper.readValue(context.body(), Message.class);
            Message updatedMessage = messageService.updateMessageText(messageId, message.getMessage_text());
            if (updatedMessage != null) {
                context.json(updatedMessage);
            } else {
                context.status(400);
            }
        } catch (Exception e) {
            context.status(400);
        }
    }

    private void getMessagesByUserIdHandler(Context context) {
        int userId = Integer.parseInt(context.pathParam("id"));
        List<Message> messages = messageService.getMessagesByUserId(userId);
        context.json(messages);
    }
}