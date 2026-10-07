package com.project.book_store;

import com.project.book_store.entity.enums.Gender;
import com.project.book_store.entity.enums.RoleType;
import com.project.book_store.payload.request.AdminRequest;
import com.project.book_store.payload.request.CustomerRequest;
import com.project.book_store.service.AdminService;
import com.project.book_store.service.CustomerService;
import com.project.book_store.service.UserRoleService;
import com.project.book_store.utils.Messages;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;

@EnableScheduling
@SpringBootApplication
public class BookStoreApplication implements CommandLineRunner {

	private final UserRoleService userRoleService;
	private final AdminService adminService;

	private final CustomerService customerService;

	public BookStoreApplication(UserRoleService userRoleService, AdminService adminService, CustomerService customerService) {
		this.userRoleService = userRoleService;
		this.adminService = adminService;
		this.customerService = customerService;
	}

	public static void main(String[] args) {
		SpringApplication.run(BookStoreApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		if (userRoleService.getAllUserRole().isEmpty()) {
			userRoleService.save(RoleType.ADMIN);
			userRoleService.save(RoleType.CUSTOMER);
			userRoleService.save(RoleType.MANAGER);
		}

		if (adminService.countAllAdmin() == 0) {
			AdminRequest admin = new AdminRequest();
			admin.setEmail(Messages.BUILT_IN_ADMIN_EMAIL);
			admin.setTc("11111111111");
			admin.setPassword("12345678");
			admin.setName("Admin");
			admin.setSurname("Admin");
			admin.setPhoneNumber("905554444331");
			admin.setGender(Gender.FEMALE);
			admin.setEnabled(true);
			admin.setBirthDate(LocalDate.of(2002, 5, 2));
			adminService.save(admin);
		}
		if (customerService.countAllCustomer() == 0) {

			CustomerRequest c1 = CustomerRequest.builder()
					.email("ahmet@mail.com")
					.tc("22222222222")
					.name("Ahmet")
					.surname("Yilmaz")
					.birthDate(LocalDate.of(1990, 1, 15))
					.password("12345678")
					.phoneNumber("905551112233")
					.gender(Gender.MALE)
					.build();

			customerService.save(c1);

			System.out.println(">> 5 Adet test müşterisi Service katmanı üzerinden başarıyla oluşturuldu.");
		}
	}
}
