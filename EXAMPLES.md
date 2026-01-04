# Usage Examples

## Example 1: Simple Multiple Choice

### Input (Screenshot/Image):
```
What is the capital of France?
A. London
B. Paris
C. Berlin
D. Madrid
```

### App Processing:
1. **OCR Extracts**: "What is the capital of France? A. London B. Paris C. Berlin D. Madrid"
2. **Parser Identifies**: 
   - Question: "What is the capital of France?"
   - Options: ["A. London", "B. Paris", "C. Berlin", "D. Madrid"]
3. **Gemini Answers**: "B. Paris"
4. **TTS Speaks**: "What is the capital of France? The correct answer is B. Paris"

### Output Display:
```
Found 1 question(s):

Question 1:
What is the capital of France?
  A. London
  B. Paris
  C. Berlin
  D. Madrid

Answer: B. Paris
--------------------------------------------------
```

---

## Example 2: Programming Quiz

### Input:
```
1. Which keyword is used to define a function in Python?
A) function
B) def
C) func
D) define

2. What does HTML stand for?
A) Hyper Text Markup Language
B) High Tech Modern Language
C) Home Tool Markup Language
D) None of the above
```

### Processing:
- Detects 2 questions
- Sends each to Gemini AI
- Speaks each answer sequentially

### Output:
```
Found 2 question(s):

Question 1:
Which keyword is used to define a function in Python?
  A) function
  B) def
  C) func
  D) define

Answer: B) def
--------------------------------------------------

Question 2:
What does HTML stand for?
  A) Hyper Text Markup Language
  B) High Tech Modern Language
  C) Home Tool Markup Language
  D) None of the above

Answer: A) Hyper Text Markup Language
--------------------------------------------------
```

---

## Example 3: Science Quiz

### Input:
```
Question 1: What is the chemical symbol for Gold?
a. Go
b. Gd
c. Au
d. Ag

Question 2: How many planets are in our solar system?
a. 7
b. 8
c. 9
d. 10
```

### Output:
```
Found 2 question(s):

Question 1:
What is the chemical symbol for Gold?
  a. Go
  b. Gd
  c. Au
  d. Ag

Answer: c. Au
--------------------------------------------------

Question 2:
How many planets are in our solar system?
  a. 7
  b. 8
  c. 9
  d. 10

Answer: b. 8
--------------------------------------------------
```

---

## Example 4: Math Quiz

### Input:
```
Q1: What is 15% of 200?
A. 20
B. 25
C. 30
D. 35

Q2: Solve: 2x + 5 = 15
A. x = 5
B. x = 10
C. x = 7.5
D. x = 4
```

### Output:
```
Found 2 question(s):

Question 1:
What is 15% of 200?
  A. 20
  B. 25
  C. 30
  D. 35

Answer: C. 30
--------------------------------------------------

Question 2:
Solve: 2x + 5 = 15
  A. x = 5
  B. x = 10
  C. x = 7.5
  D. x = 4

Answer: A. x = 5
--------------------------------------------------
```

---

## Common Patterns Recognized

### Numbered Questions
✅ `1.`, `2.`, `3.`
✅ `Q1:`, `Q2:`, `Q3:`
✅ `Question 1:`, `Question 2:`

### Option Formats
✅ `A.`, `B.`, `C.`, `D.`
✅ `A)`, `B)`, `C)`, `D)`
✅ `a.`, `b.`, `c.`, `d.`
✅ `(A)`, `(B)`, `(C)`, `(D)`

### Question Indicators
✅ Ends with `?`
✅ Starts with number
✅ Starts with "Question" or "Q"
✅ Long text followed by options

---

## Real-World Use Cases

### 1. Online Quizzes
- Take screenshot of quiz on computer screen
- App extracts and answers questions
- Review answers before submitting

### 2. Study Materials
- Photograph textbook practice questions
- Get instant answers for self-study
- Hear explanations via TTS while studying

### 3. Exam Practice
- Screenshot practice tests
- Compare your answers with AI suggestions
- Learn from detailed explanations

### 4. Homework Help
- Capture homework questions
- Get step-by-step guidance
- Understand concepts better

---

## Tips for Best Results

### 📸 Image Quality
- ✅ Clear, high-resolution images
- ✅ Good lighting, no shadows
- ✅ Straight text orientation
- ❌ Avoid blurry or tilted images
- ❌ Don't include too much background

### 📝 Question Format
- ✅ Clearly separated questions
- ✅ Distinct option markers (A, B, C, D)
- ✅ Complete sentences
- ❌ Avoid overlapping text
- ❌ Don't mix multiple question types

### 🎯 Accuracy
- ✅ Double-check AI answers
- ✅ Use for studying, not cheating
- ✅ Verify against reliable sources
- ⚠️ AI can make mistakes
- ⚠️ Consider context and nuance

---

## Troubleshooting Examples

### Problem: "No questions found"

**Possible Input:**
```
The sky is blue because of light scattering.
Water boils at 100°C.
```

**Why**: No question marks or option patterns detected.

**Solution**: Ensure text contains proper question format with A/B/C/D options.

---

### Problem: Incorrect OCR

**Bad Image Quality:**
```
Wh@t is th3 c@pit@l 0f Fr@nc3?
A. L0nd0n
```

**Solution**: 
- Improve image quality
- Ensure proper lighting
- Use clearer fonts
- Avoid handwriting if possible

---

### Problem: Wrong Answer

**Complex Question:**
```
In the context of quantum mechanics, which interpretation
addresses the measurement problem?
A. Copenhagen
B. Many-worlds
C. Both
D. Neither
```

**Why**: Very specialized or ambiguous questions may confuse AI.

**Solution**: 
- Verify answers independently
- Consider question clarity
- Use for general knowledge, not specialized exams

---

## Expected Response Times

| Task | Approximate Time |
|------|-----------------|
| Screenshot capture | 1-2 seconds |
| OCR processing | 2-3 seconds |
| Question parsing | < 1 second |
| Gemini AI answer (per question) | 2-5 seconds |
| Text-to-Speech | 3-5 seconds per question |

**Total for 5 questions**: ~30-45 seconds

---

## API Usage Estimates

### Free Tier (Gemini)
- ~15 queries/minute
- ~1,500 queries/day

### Token Usage Per Question
- Simple question: 50-100 tokens
- Complex question: 200-500 tokens
- Average: 150 tokens per question

### Example Cost (if paid tier)
- 10 questions = ~1,500 tokens ≈ $0.00075
- 100 questions = ~15,000 tokens ≈ $0.0075
- Very affordable for most use cases!

---

**Remember**: This app is designed for learning and practice. Always verify important answers and use responsibly! 📚✨
