package hokumei.sys.matchfriends.service;

import jakarta.annotation.Resource;
import org.springframework.transaction.support.TransactionTemplate;

public class TransactionalTest {
	static class Student{
		String name;
		int id;
	}
	@Resource
	private TransactionTemplate transactionTemplate;

	@Resource
	private UserService userService;


//	@GetMapping("/db/test2")
//	public String testdb2(int num) {
//		Student student = Student.builder().id(1).name("张三").score(100).build();
//		User user = User.builder().id(1).name("张三").age(11).sex(1).build();
//
//		// 有返回值的编程式事务
//		transactionTemplate.execute(new TransactionCallback<Object>() {
//			@Override
//			public Object doInTransaction(TransactionStatus status) {
//				try {
//					userMapper.insertUser(user);
//					System.out.println(1 / num); // 模拟可能出现的异常
//					studentMapper.insertStudent(student);
//					return "ok";
//				} catch (Exception e) {
//					e.printStackTrace();
//					status.setRollbackOnly(); // 手动回滚事务
//					return null;
//				}
//			}
//		});
//
//		return "ok";
//	}
//
//	@GetMapping("/db/test3")
//	public String testdb3(int num) {
//		User user = User.builder().id(1).name("张三").age(11).sex(1).build();
//
//		// 没有返回值的编程式事务
//		transactionTemplate.executeWithoutResult(new Consumer<TransactionStatus>() {
//			@Override
//			public void accept(TransactionStatus status) {
//				try {
//					userMapper.insertUser(user);
//					System.out.println(1 / num); // 模拟可能出现的异常
//				} catch (Exception e) {
//					e.printStackTrace();
//					status.setRollbackOnly(); // 手动回滚事务
//				}
//			}
//		});
//
//		return "ok";
//	}

}
