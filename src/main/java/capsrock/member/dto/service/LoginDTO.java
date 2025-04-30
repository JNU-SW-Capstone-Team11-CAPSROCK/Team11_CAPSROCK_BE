package capsrock.member.dto.service;

import capsrock.member.model.vo.Email;
import capsrock.member.model.vo.EncryptedPassword;

public record LoginDTO(Email email, EncryptedPassword encryptedPassword) {

}
