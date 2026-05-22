# CLAUDE.md — MindBridge
### A Mental Health Social Platform with AI Emotion Intelligence

---

## What is this?

MindBridge is a mental health peer-support platform where people share how they feel, and the community responds — guided by an AI layer that understands the emotional weight behind every post.

It is **not** a therapy app. It is **not** a diagnosis tool.
It is a safe space where human empathy and AI intelligence work together to make people feel heard, understood, and less alone.

---

## The Problem

Most people experiencing anxiety, sadness, or emotional distress have nowhere to go that feels:
- **Safe enough** to be vulnerable
- **Fast enough** to get support when they need it
- **Smart enough** to give real, personalized guidance

Therapy has waitlists. Twitter is toxic. Friends don't always know what to say.
MindBridge fills that gap.

---

## Core Features

### 1. Emotional Feed
- Users post how they're feeling — text, voice note, or both
- Posts are **anonymous by default** — users choose to reveal identity
- Feed is filtered by emotional state, not by popularity or engagement
- No likes. No viral mechanics. Only support.

### 2. AI Emotion Detection (the core innovation)
Every post passes through a two-layer AI pipeline:

**Layer 1 — Fast Emotion Classifier**
- Model: `j-hartmann/emotion-english-distilroberta-base` (HuggingFace)
- Detects: joy, sadness, anger, fear, disgust, surprise, neutral
- Also analyzes post *structure*: sentence fragmentation, punctuation patterns, vocabulary density, repetition — signals that go beyond simple sentiment
- Runs on every post in milliseconds, silently

**Layer 2 — AI Agent (LLM)**
- Takes emotion tags + post content
- Generates a **private message to the author** with personalized feedback and recommendations
- Performs severity triage: `low / medium / high`
- If high-risk language is detected → immediately surfaces professional crisis resources

### 3. Community Replies — Guided by AI Context
When a user opens a post to reply, they see a subtle AI-generated context card:

> *"This person seems to be feeling anxious and overwhelmed — reply with patience and care"*

This shapes the tone of the community without being prescriptive.
Human empathy, AI-directed.

### 4. Empathy Reactions
No thumbs up. No hearts. Only:
- 🤝 *I hear you*
- 💙 *You're not alone*
- 🌱 *This takes courage*
- 🔥 *I felt this too*

### 5. Personal Mood Timeline
- Only visible to the user
- A private graph of emotional state over time, derived from their posts
- Helps users notice patterns: *"I tend to feel this way on Sundays"*

---

## Auth System (already built ✅)

Three-factor authentication — critical for a platform handling sensitive emotional data:

1. **Username + Password**
2. **SMS OTP via Twilio** — one-time code sent to phone
3. **Voice Recognition** — user's voice as biometric confirmation

This level of auth signals to users that their data is **protected and private**.
It is also a technical differentiator — most platforms don't come close to this.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | FastAPI (Python) |
| Auth | Existing 3FA system (Twilio + Voice Recognition) |
| Emotion ML | HuggingFace Transformers (`distilroberta`, fine-tuned) |
| AI Agent | Claude API (Anthropic) |
| Database | PostgreSQL |
| Real-time | WebSockets (live replies, notifications) |
| Frontend | Next.js (React) |
| Hosting | Docker + cloud provider (Railway, Render, or AWS) |

---

## System Architecture

```
┌─────────────────────────────────────────────────────┐
│                    FRONTEND (Next.js)                │
│         Feed · Post · Reply · Mood Timeline         │
└────────────────────────┬────────────────────────────┘
                         │ REST + WebSocket
┌────────────────────────▼────────────────────────────┐
│                  BACKEND (FastAPI)                   │
│                                                      │
│  ┌─────────────┐   ┌──────────────┐   ┌──────────┐  │
│  │  Auth Layer │   │  Posts API   │   │ Feed API │  │
│  │  (3FA ✅)   │   │  (CRUD)      │   │ (filter) │  │
│  └─────────────┘   └──────┬───────┘   └──────────┘  │
│                           │                          │
│              ┌────────────▼────────────┐             │
│              │   Emotion Pipeline      │             │
│              │                         │             │
│              │  [HuggingFace Model]    │             │
│              │  Emotion tags + Risk    │             │
│              │         ↓               │             │
│              │  [LLM Agent]            │             │
│              │  Private feedback       │             │
│              │  + Recommendations      │             │
│              └─────────────────────────┘             │
│                                                      │
└──────────────────────────┬──────────────────────────┘
                           │
             ┌─────────────▼──────────┐
             │     PostgreSQL DB       │
             │  users · posts · replies│
             │  emotions · timelines   │
             └─────────────────────────┘
```

---

## Data Models (simplified)

```python
# User
user_id, username, email, phone, voice_print, created_at, is_anonymous

# Post
post_id, user_id, content, voice_note_url, is_anonymous,
emotion_tags, severity_level, ai_feedback_sent, created_at

# Reply
reply_id, post_id, user_id, content, is_anonymous,
ai_context_shown, created_at

# EmotionLog
log_id, user_id, post_id, primary_emotion, secondary_emotion,
confidence_score, structure_signals, severity, created_at
```

---

## Emotion Detection — ML Detail

Beyond the pre-trained classifier, the model also evaluates **structural signals**:

| Signal | What it indicates |
|---|---|
| Short fragmented sentences | Distress, overwhelm |
| Ellipses and trailing off... | Uncertainty, resignation |
| All lowercase, no punctuation | Low energy, depression |
| Repetition of words/phrases | Rumination, anxiety loops |
| Vocabulary density drop | Cognitive load, numbness |

This is the differentiator. Most apps do positive/negative sentiment.
MindBridge does **nuanced emotional state mapping**.

---

## AI Safety Layer

| Severity | Action |
|---|---|
| Low | Private AI feedback + community sees post normally |
| Medium | AI feedback includes coping strategies + community guided |
| High | Immediate crisis resources surfaced to user (private) + post optionally hidden from feed |

**Disclaimer always present:** MindBridge is peer support, not professional therapy.
In crisis? Resources are always one tap away.

---

## Why This Impresses

This project demonstrates:

- **ML/NLP** — emotion classification + fine-tuned structural analysis
- **AI Agents** — LLM-powered personalized feedback pipeline
- **Real-time systems** — WebSockets for live community interaction
- **Security** — 3FA auth on sensitive personal data
- **Product thinking** — ethical design decisions (anonymous, no viral mechanics, triage layer)
- **Social impact** — solving a real problem affecting millions of people

It is not a tutorial project. It is not a CRUD app.
It is a system that required real architectural decisions — and it helps people.

---

## Development Phases

### Phase 1 — Core (MVP)
- [ ] Migrate existing auth system into new project
- [ ] Post creation (text only first)
- [ ] Emotion detection pipeline (HuggingFace classifier)
- [ ] Basic feed (chronological, emotion-tagged)
- [ ] Reply system

### Phase 2 — AI Layer
- [ ] LLM agent for private user feedback
- [ ] AI context card for responders
- [ ] Severity triage + crisis resource surfacing
- [ ] Structural signal analysis (fine-tuning)

### Phase 3 — Social Polish
- [ ] Anonymous posting toggle
- [ ] Empathy reactions
- [ ] Personal mood timeline
- [ ] Voice note posts

### Phase 4 — Production
- [ ] Voice note integration (leverages existing voice recognition)
- [ ] Performance optimization
- [ ] Docker deployment
- [ ] Privacy policy + mental health disclaimers

---

## Project Values

1. **Privacy first** — anonymous by default, 3FA protected
2. **Empathy over engagement** — no viral mechanics, no gamification of pain
3. **AI assists, humans connect** — the AI never replaces human support
4. **Safety is non-negotiable** — crisis detection is always on

---

*Built by a software engineer who believes code can give people a better quality of life.*
