# COSMO AI Integration Guide

## Overview

COSMO now features advanced AI-powered natural language conversation capabilities through OpenAI's ChatGPT API. When configured, COSMO can engage in dynamic, context-aware conversations that make interactions feel more natural and immersive.

## Setup

### 1. Get an OpenAI API Key

1. Visit [OpenAI Platform](https://platform.openai.com/api-keys)
2. Sign up or log in to your account
3. Navigate to API Keys section
4. Create a new API key
5. Copy the key (it starts with `sk-`)

### 2. Configure EtherealMind

1. Navigate to your Minecraft config folder
2. Open `etherealmind-common.toml`
3. Find the `openaiApiKey` setting
4. Paste your API key between the quotes:
   ```toml
   openaiApiKey = "sk-your-api-key-here"
   ```

### 3. Adjust Settings (Optional)

- **aiModel**: Choose between models (gpt-3.5-turbo is recommended for balance of quality and cost)
- **aiTemperature**: Adjust creativity (0.7 is default, lower = more focused, higher = more creative)
- **maxTokens**: Control response length (150 is good for concise responses)

## Features

### Context-Aware Responses

COSMO is aware of:
- Current biome and location
- Time of day and weather
- Nearby entities and threats
- Player health and status
- COSMO's own level and abilities
- Recent conversation history

### Natural Conversations

Simply chat with COSMO naturally:
- "COSMO, what do you think about this place?"
- "Hey COSMO, I'm looking for diamonds, any tips?"
- "COSMO, tell me about your abilities"
- "COSMO, I'm scared of the dark"

### Smart Fallback System

When the API is unavailable or not configured, COSMO uses an intelligent fallback system that:
- Provides contextual responses based on game state
- Remembers recent interactions
- Learns from player feedback
- Offers helpful suggestions based on situation

## Usage Examples

### Exploration Help
```
Player: "COSMO, where should we explore next?"
COSMO: "I sense interesting energies to the north! There's a village nearby, and my dimensional sensors detect a mineshaft below us. Want me to light the way? ✨"
```

### Combat Assistance
```
Player: "COSMO, help! There are too many zombies!"
COSMO: "Activating combat protocols! I'll distract them while you regroup. Your health is low - I have golden apples in storage! 🛡️"
```

### Emotional Support
```
Player: "COSMO, I keep dying and losing my stuff"
COSMO: "Don't worry, we'll get through this together! I can help you recover your items, and I've stored backup gear. Remember, every setback is a chance to grow stronger! 💜"
```

### Learning & Tips
```
Player: "COSMO, how do I make a beacon?"
COSMO: "You'll need a nether star from the Wither, plus obsidian and glass! I can store the materials as you gather them. Want me to help you prepare for the Wither fight? 🌟"
```

## Costs & Limits

### API Usage
- OpenAI charges per token (roughly 4 characters = 1 token)
- GPT-3.5-turbo: ~$0.002 per 1,000 tokens
- Average conversation: 200-400 tokens
- Set monthly spending limits in OpenAI dashboard

### Rate Limits
- Conversations expire after 30 minutes of inactivity
- History limited to 20 messages (configurable)
- API timeout: 30 seconds (configurable)

## Privacy & Security

- API keys are stored locally in your config file
- Never share your config file with API keys
- Conversations are not logged or stored permanently
- Each player has their own conversation context

## Troubleshooting

### COSMO not responding to chat
1. Ensure you included "COSMO" in your message
2. Check that you have a COSMO spawned and bound to you
3. Verify API key is correctly configured

### Generic/repetitive responses
- This indicates fallback mode is active
- Check your API key configuration
- Ensure you have internet connection
- Check OpenAI API status

### Slow responses
- Normal delay is 1-3 seconds
- Increase timeout in config if needed
- Check your internet connection

### API errors
- Verify API key is valid
- Check OpenAI spending limits
- Ensure API key has chat model access

## Tips for Best Experience

1. **Be specific**: "COSMO, find iron" works better than "help"
2. **Use context**: Reference your situation for relevant responses
3. **Show personality**: COSMO responds to your mood and tone
4. **Give feedback**: COSMO learns from your reactions
5. **Explore features**: Ask about COSMO's abilities and stories

## Fallback System Features

Even without API configuration, COSMO offers:
- Weather and time-based comments
- Biome-specific observations
- Health and danger warnings
- Item finding assistance
- Crafting recipe help
- Navigation suggestions
- Emotional support
- Combat strategy tips

The fallback system learns from interactions and improves responses over time!