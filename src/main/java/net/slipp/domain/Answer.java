package net.slipp.domain;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Answer extends AbstractEntity {
	@ManyToOne
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_writer"))
	@JsonProperty
	private User writer;
	
	@ManyToOne
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_to_question"))
	@JsonProperty
	private Question question;
	
	@Lob
	@JsonProperty
	private String contents;

	public Answer() {}
	
	public Answer(User writer, Question question, String contents) {
		this.writer = writer;
		this.contents = contents;
		this.question = question;
	}
	
	@Override
	public String toString() {
		return "Answer [" + super.toString() + "]";
	}

	public boolean isSameWriter(User loginUser) {
		return loginUser.equals(this.writer);
	}
	
}
