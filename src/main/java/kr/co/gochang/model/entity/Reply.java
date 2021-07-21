package kr.co.gochang.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "connector")
@Table(name = "connector")
@Builder
@Accessors(chain = true)
public class Reply {
}
