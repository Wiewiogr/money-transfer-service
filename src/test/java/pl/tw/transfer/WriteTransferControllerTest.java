package pl.tw.transfer;

import com.google.gson.Gson;
import org.apache.log4j.BasicConfigurator;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.tw.account.AccountBalanceRepository;
import pl.tw.account.AccountRepository;
import pl.tw.http.HttpResponse;
import spark.Request;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class WriteTransferControllerTest {

    private final Gson gson = new Gson();

    @BeforeClass
    public void setUp() {
        BasicConfigurator.configure();
    }

    @Test
    public void shouldRecordTransferWhenAccountHasMoreMoneyThanIsNeeded() {
        //Given
        TransferRepository transferRepository = mock(TransferRepository.class);
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountBalanceRepository accountBalanceRepository = mock(AccountBalanceRepository.class);

        WriteTransferController writeTransferController = new WriteTransferController(
                transferRepository,
                accountRepository,
                accountBalanceRepository
        );

        TransferRequest transferRequest = createTransferRequest(new BigDecimal("100.0"), "Title");
        String jsonTransferRequest = gson.toJson(transferRequest);

        Request request = mock(Request.class);
        when(request.body()).thenReturn(jsonTransferRequest);
        when(accountRepository.accountExists(transferRequest.getFrom())).thenReturn(true);
        when(accountRepository.accountExists(transferRequest.getTo())).thenReturn(true);
        when(accountBalanceRepository.getBalance(transferRequest.getFrom())).thenReturn(new BigDecimal("200.0"));

        UUID transferId = UUID.randomUUID();
        when(transferRepository.recordTransfer(transferRequest)).thenReturn(transferId);

        //When
        HttpResponse<UUID> result = writeTransferController.recordTransfer(request);

        //Then
        assertThat(result.isError()).isFalse();
        assertThat(result.getStatus()).isEqualTo(200);
        assertThat(result.getObject()).isEqualTo(transferId);
    }

    @Test
    public void shouldRecordTransferWhenAccountHasTheSameAmountOfMoneyAsInTransfer() {
        //Given
        TransferRepository transferRepository = mock(TransferRepository.class);
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountBalanceRepository accountBalanceRepository = mock(AccountBalanceRepository.class);

        WriteTransferController writeTransferController = new WriteTransferController(
                transferRepository,
                accountRepository,
                accountBalanceRepository
        );

        TransferRequest transferRequest = createTransferRequest(new BigDecimal("100.0"), "Title");
        String jsonTransferRequest = gson.toJson(transferRequest);

        Request request = mock(Request.class);
        when(request.body()).thenReturn(jsonTransferRequest);
        when(accountRepository.accountExists(transferRequest.getFrom())).thenReturn(true);
        when(accountRepository.accountExists(transferRequest.getTo())).thenReturn(true);
        when(accountBalanceRepository.getBalance(transferRequest.getFrom())).thenReturn(new BigDecimal("100.0"));

        UUID transferId = UUID.randomUUID();
        when(transferRepository.recordTransfer(transferRequest)).thenReturn(transferId);

        //When
        HttpResponse<UUID> result = writeTransferController.recordTransfer(request);

        //Then
        assertThat(result.isError()).isFalse();
        assertThat(result.getStatus()).isEqualTo(200);
        assertThat(result.getObject()).isEqualTo(transferId);
    }

    private String unparsableBody = "{]sdfasjnfsdncvz";

    @Test
    public void shouldReturnErrorMessageWhenErrorDuringParsingOccurs() {
        //Given
        TransferRepository transferRepository = mock(TransferRepository.class);
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountBalanceRepository accountBalanceRepository = mock(AccountBalanceRepository.class);

        WriteTransferController writeTransferController = new WriteTransferController(
                transferRepository,
                accountRepository,
                accountBalanceRepository
        );

        Request request = mock(Request.class);
        when(request.body()).thenReturn(unparsableBody);

        //When
        HttpResponse<UUID> result = writeTransferController.recordTransfer(request);

        //Then
        assertThat(result.isError()).isTrue();
        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getError()).isEqualTo("Error parsing request body.");
    }

    private static TransferRequest createTransferRequest(BigDecimal amount, String title) {
        return new TransferRequest(UUID.randomUUID(), UUID.randomUUID(), amount, title);
    }

    @Test
    public void shouldReturnNotFoundWhenFromUserWasNotFound() {
        //Given
        TransferRepository transferRepository = mock(TransferRepository.class);
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountBalanceRepository accountBalanceRepository = mock(AccountBalanceRepository.class);

        WriteTransferController writeTransferController = new WriteTransferController(
                transferRepository,
                accountRepository,
                accountBalanceRepository
        );

        TransferRequest transferRequest = createTransferRequest(new BigDecimal("100.0"), "Title");
        String jsonTransferRequest = gson.toJson(transferRequest);

        Request request = mock(Request.class);
        when(request.body()).thenReturn(jsonTransferRequest);
        when(accountRepository.accountExists(transferRequest.getFrom())).thenReturn(false);

        //When
        HttpResponse<UUID> result = writeTransferController.recordTransfer(request);

        //Then
        assertThat(result.isError()).isTrue();
        assertThat(result.getStatus()).isEqualTo(404);
        assertThat(result.getError()).isEqualTo("User " + transferRequest.getFrom() + " not found.");
    }

    @Test
    public void shouldReturnNotFoundWhenToUserWasNotFound() {
        //Given
        TransferRepository transferRepository = mock(TransferRepository.class);
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountBalanceRepository accountBalanceRepository = mock(AccountBalanceRepository.class);

        WriteTransferController writeTransferController = new WriteTransferController(
                transferRepository,
                accountRepository,
                accountBalanceRepository
        );

        TransferRequest transferRequest = createTransferRequest(new BigDecimal("100.0"), "Title");
        String jsonTransferRequest = gson.toJson(transferRequest);

        Request request = mock(Request.class);
        when(request.body()).thenReturn(jsonTransferRequest);
        when(accountRepository.accountExists(transferRequest.getFrom())).thenReturn(true);
        when(accountRepository.accountExists(transferRequest.getTo())).thenReturn(false);

        //When
        HttpResponse<UUID> result = writeTransferController.recordTransfer(request);

        //Then
        assertThat(result.isError()).isTrue();
        assertThat(result.getStatus()).isEqualTo(404);
        assertThat(result.getError()).isEqualTo("User " + transferRequest.getTo() + " not found.");
    }

    @Test
    public void shouldReturnNotFoundWhenBothUsersWhereNotFound() {
        //Given
        TransferRepository transferRepository = mock(TransferRepository.class);
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountBalanceRepository accountBalanceRepository = mock(AccountBalanceRepository.class);

        WriteTransferController writeTransferController = new WriteTransferController(
                transferRepository,
                accountRepository,
                accountBalanceRepository
        );

        TransferRequest transferRequest = createTransferRequest(new BigDecimal("100.0"), "Title");
        String jsonTransferRequest = gson.toJson(transferRequest);

        Request request = mock(Request.class);
        when(request.body()).thenReturn(jsonTransferRequest);
        when(accountRepository.accountExists(transferRequest.getFrom())).thenReturn(false);
        when(accountRepository.accountExists(transferRequest.getTo())).thenReturn(false);

        //When
        HttpResponse<UUID> result = writeTransferController.recordTransfer(request);

        //Then
        assertThat(result.isError()).isTrue();
        assertThat(result.getStatus()).isEqualTo(404);
        assertThat(result.getError()).isEqualTo("User " + transferRequest.getFrom() + " not found.");
    }

    @Test
    public void shouldReturnBadRequestWhenDoesNotHaveEnoughMoney() {
        //Given
        TransferRepository transferRepository = mock(TransferRepository.class);
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountBalanceRepository accountBalanceRepository = mock(AccountBalanceRepository.class);

        WriteTransferController writeTransferController = new WriteTransferController(
                transferRepository,
                accountRepository,
                accountBalanceRepository
        );

        TransferRequest transferRequest = createTransferRequest(new BigDecimal("100.0"), "Title");
        String jsonTransferRequest = gson.toJson(transferRequest);

        Request request = mock(Request.class);
        when(request.body()).thenReturn(jsonTransferRequest);
        when(accountRepository.accountExists(transferRequest.getFrom())).thenReturn(true);
        when(accountRepository.accountExists(transferRequest.getTo())).thenReturn(true);
        when(accountBalanceRepository.getBalance(transferRequest.getFrom())).thenReturn(new BigDecimal("50.0"));

        //When
        HttpResponse<UUID> result = writeTransferController.recordTransfer(request);

        //Then
        assertThat(result.isError()).isTrue();
        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getError()).isEqualTo("User " + transferRequest.getFrom() + " do not have enough money");
    }
}