package com.aminnorouzi.customerservice.service;

import com.aminnorouzi.customerservice.client.AccountClient;
import com.aminnorouzi.customerservice.exception.CustomerNotFoundException;
import com.aminnorouzi.customerservice.exception.IllegalCustomerDeleteException;
import com.aminnorouzi.customerservice.exception.IllegalNationalCodeException;
import com.aminnorouzi.customerservice.exception.NationalCodeWasTakenException;
import com.aminnorouzi.customerservice.model.Customer;
import com.aminnorouzi.customerservice.model.CustomerRequest;
import com.aminnorouzi.customerservice.model.Status;
import com.aminnorouzi.customerservice.model.Type;
import com.aminnorouzi.customerservice.model.account.Account;
import com.aminnorouzi.customerservice.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private AccountClient accountClient;
    @InjectMocks
    private CustomerService customerService;

    @Test
    void shouldCreateCustomer() {
        // given
        CustomerRequest request = CustomerRequest.builder()
                .fullName("Amin Norouzi")
                .nationalCode("0123456789")
                .phoneNumber("012345678998")
                .type(Type.INDIVIDUAL)
                .birthDate(LocalDate.parse("2000-01-01"))
                .build();

        Customer customer = Customer.builder()
                .fullName(request.getFullName())
                .nationalCode(request.getNationalCode())
                .phoneNumber(request.getPhoneNumber())
                .type(request.getType())
                .birthDate(request.getBirthDate())
                .status(Status.ACTIVE)
                .createdAt(LocalDate.now())
                .build();

        // when
        customerService.createCustomer(request);

        // then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerRepository, times(1)).save(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer).isEqualTo(customer);
    }

    @Test
    void shouldNotCreateCustomerWhenNationalCodeIsNullOrEmpty() {
        // given
        CustomerRequest request1 = CustomerRequest.builder()
                .fullName("Amin Norouzi")
                .nationalCode(null)
                .phoneNumber("012345678998")
                .type(Type.INDIVIDUAL)
                .birthDate(LocalDate.parse("2000-01-01"))
                .build();

        CustomerRequest request2 = CustomerRequest.builder()
                .fullName("Amin Norouzi")
                .nationalCode("")
                .phoneNumber("012345678998")
                .type(Type.INDIVIDUAL)
                .birthDate(LocalDate.parse("2000-01-01"))
                .build();

        // when
        // then
        assertThatThrownBy(() -> customerService.createCustomer(request1))
                .isInstanceOf(IllegalNationalCodeException.class)
                .hasMessageContaining("National code can not be null or empty!");

        assertThatThrownBy(() -> customerService.createCustomer(request2))
                .isInstanceOf(IllegalNationalCodeException.class)
                .hasMessageContaining("National code can not be null or empty!");

        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldNotCreateCustomerWhenNationalCodeIsTaken() {
        // given
        CustomerRequest request = CustomerRequest.builder()
                .fullName("Amin Norouzi")
                .nationalCode("0123456789")
                .phoneNumber("012345678998")
                .type(Type.INDIVIDUAL)
                .birthDate(LocalDate.parse("2000-01-01"))
                .build();

        given(customerRepository.existsByNationalCodeEquals(request.getNationalCode()))
                .willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isInstanceOf(NationalCodeWasTakenException.class)
                .hasMessageContaining(String.format("National code: %s is already taken!", request.getNationalCode()));

        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldNotCreateCustomerWhenNationalCodeIsNot10Digits() {
        // given
        CustomerRequest request1 = CustomerRequest.builder()
                .fullName("Amin Norouzi")
                .nationalCode("0123456789abc")
                .phoneNumber("012345678998")
                .type(Type.INDIVIDUAL)
                .birthDate(LocalDate.parse("2000-01-01"))
                .build();

        CustomerRequest request2 = CustomerRequest.builder()
                .fullName("Amin Norouzi")
                .nationalCode("01234")
                .phoneNumber("012345678998")
                .type(Type.INDIVIDUAL)
                .birthDate(LocalDate.parse("2000-01-01"))
                .build();

        // when
        // then
        assertThatThrownBy(() -> customerService.createCustomer(request1))
                .isInstanceOf(IllegalNationalCodeException.class)
                .hasMessageContaining("National code must be 10 digits only!");

        assertThatThrownBy(() -> customerService.createCustomer(request2))
                .isInstanceOf(IllegalNationalCodeException.class)
                .hasMessageContaining("National code must be 10 digits only!");

        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldDeleteCustomer() {
        // given
        long id = 10;
        given(customerRepository.existsByIdEquals(id))
                .willReturn(true);

        given(customerService.getAccountsByCustomerId(id))
                .willReturn(Collections.emptyList());

        // when
        // then
        customerService.deleteCustomer(id);

        verify(customerRepository, times(1)).deleteById(id);
    }

    @Test
    void shouldNotDeleteCustomerWhenNotFound() {
        // given
        long id = 10;
        given(customerRepository.existsByIdEquals(id))
                .willReturn(false);
        // when
        // then
        assertThatThrownBy(() -> customerService.deleteCustomer(id))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining(String.format("Customer: %s not found!", id));

        verify(customerRepository, never()).deleteById(any());
    }

    @Test
    void shouldNotDeleteCustomerWhenHasAccounts() {
        // given
        long id = 10;
        given(customerRepository.existsByIdEquals(id))
                .willReturn(true);

        given(customerService.getAccountsByCustomerId(id))
                .willReturn(List.of(new Account()));

        // when
        // then
        assertThatThrownBy(() -> customerService.deleteCustomer(id))
                .isInstanceOf(IllegalCustomerDeleteException.class)
                .hasMessageContaining(String.format("Customer: %s has some accounts!", id));

        verify(customerRepository, never()).deleteById(any());
    }

    @Test
    void shouldVerifyCustomer() {
        // given
        long id = 10;
        given(customerRepository.existsByIdEquals(id))
                .willReturn(true);

        // when
        // then
        assertThat(customerService.verifyCustomer(id)).isTrue();

        verify(customerRepository, times(1)).existsByIdEquals(id);
    }

    @Test
    void shouldNotVerifyCustomerWhenNotFound() {
        // given
        long id = 10;
        given(customerRepository.existsByIdEquals(id))
                .willReturn(false);

        // when
        // then
        assertThat(customerService.verifyCustomer(id)).isFalse();

        verify(customerRepository, times(1)).existsByIdEquals(id);
    }

    @Test
    void shouldChangeCustomerStatus() {
        // given
        long id = 10;
        String status = "active";
        given(customerRepository.findById(id))
                .willReturn(Optional.of(new Customer()));

        // when
        // then
        customerService.changeCustomerStatus(id, status);

        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void shouldNotChangeCustomerStatusWhenCustomerNotFound() {
        // given
        long id = 10;
        String status = "active";
        given(customerRepository.findById(id))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> customerService.changeCustomerStatus(id, status))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining(String.format("Customer: %s not found!", id));

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldNotChangeCustomerStatusWhenIsInvalid() {
        // given
        long id = 10;
        String status = "active???";
        given(customerRepository.findById(id))
                .willReturn(Optional.of(new Customer()));

        // when
        // then
        assertThatThrownBy(() -> customerService.changeCustomerStatus(id, status))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.format("No enum constant %s.ACTIVE???", Status.class.getName()));

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldGetCustomerById() {
        // given
        long id = 10;
        given(customerRepository.findById(id))
                .willReturn(Optional.of(new Customer()));

        // when
        // then
        customerService.getCustomerById(id);

        verify(customerRepository, times(1)).findById(id);
    }

    @Test
    void shouldNotGetCustomerByIdWhenNotFound() {
        // given
        long id = 10;
        given(customerRepository.findById(id))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> customerService.getCustomerById(id))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining(String.format("Customer: %s not found!", id));

        verify(customerRepository, times(1)).findById(id);
    }

    @Test
    void shouldGetCustomersByFilter() {
        // when
        customerService.getCustomersByFilter(any(Specification.class));

        // then
        verify(customerRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void shouldGetAccountsByCustomerId() {
        // given
        long id = 10;
        given(customerRepository.existsByIdEquals(id))
                .willReturn(true);

        // when
        // then
        customerService.getAccountsByCustomerId(id);

        verify(accountClient, times(1)).getAccountsByCustomer(id);
    }

    @Test
    void shouldNotGetAccountsByCustomerIdWhenCustomerNotFound() {
        // given
        long id = 10;
        given(customerRepository.existsByIdEquals(id))
                .willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> customerService.getAccountsByCustomerId(id))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining(String.format("Customer: %s not found!", id));

        verify(accountClient, never()).getAccountsByCustomer(id);
    }
}