<template>
  <div>
    <b-dropdown-item to="/personality-test">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.personalityTest')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/question">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.question')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/answer">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.answer')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/user-response">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.userResponse')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/test-result">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.testResult')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/personality-type">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.personalityType')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/user-subscription">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.userSubscription')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/consultation">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.consultation')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/consultant">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.consultant')"></span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>
