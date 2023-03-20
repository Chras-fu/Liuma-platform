<template>
    <editor v-model="content" :lang="mode" @init="editorInit" :theme="theme" width="100%" :height="height"/>
</template>

<script>
    export default {
      name: "CodeEdit",
      components: { editor: require('vue2-ace-editor')},
      props: {
        data:  {
          type:String
        },
        height: {
          type:Number
        },
        theme: {
          type: String,
          default() {
            return 'chrome'
          }
        },
        readOnly: {
          type: Boolean,
          default() {
            return false;
          }
        },
        mode: {
          type: String,
          default() {
            return 'text';
          }
        },
        modes: {
          type: Array,
          default() {
            return ['text', 'xml', 'html', 'python', 'sql'];
          }
        }
      },
      watch: {
        content(){
          this.$emit('update:data', this.content);
        },
        data(){
          this.content = this.data;
        }
      },
      data() {
        return {
          content: null
        }
      },
      mounted() {
        this.content = this.data;
      },
      methods: {
        editorInit: function (editor) {
          require('brace/ext/language_tools') //language extension prerequsite...
          this.modes.forEach(mode => {
            require('brace/mode/' + mode); //language
          });
          require('brace/theme/' + this.theme)
          require('brace/snippets/javascript') //snippet
          if (this.readOnly) {
            editor.setReadOnly(true);
          }
        },
      }
    }
</script>

<style scoped>

</style>
