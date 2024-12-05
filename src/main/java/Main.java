import Controller.SocialMediaController;
import DAO.AccountDAO;
import DAO.MessageDAO;
import Service.AccountService;
import Service.MessageService;


/**
 * This class is provided with a main method to allow you to manually run and test your application. This class will not
 * affect your program in any way and you may write whatever code you like here.
 */
public class Main {
    public static void main(String[] args) {
        AccountDAO accountDAO = new AccountDAO();
        MessageDAO messageDAO = new MessageDAO();

        AccountService accountService = new AccountService(accountDAO);
        MessageService messageService = new MessageService(messageDAO);

        SocialMediaController controller = new SocialMediaController(accountService, messageService);

        controller.startAPI().start(8080);
    }
}