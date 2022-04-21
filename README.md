# security_CustomeAuthenticationProvider

Tham khảo file WebSecurityConfig.java và file CustomeAuthenticationProvider.java

Trường hợp này là dạng user/pass không được lưu trữ trên CSLD của app mà app sẽ login thông qua một bên thứ 3
chỉ biết username, không biết pass => sẽ convert các thông tin này vào CustomeAuthenticationProvider, cụ thể là: UsernamePasswordAuthenticationToken
