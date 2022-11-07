import os

source_in_dir = 'INSERT_CORRECT_PATH/SiVa/siva-parent/siva-test/src/test/java/ee/openeid/siva/'

Test_cases = open('INSERT_CORRECT_PATH/siva_tc.md','w', encoding="utf8")
Test_cases.write("Test Case Descriptions\n")
Test_cases.write("==================\n")
Test_cases.write("\n## Introduction\n")
Test_cases.write("This document gives overview of the test cases. This page is partially generated using a script, more info about the script can be found in [SiVa GitHub](https://github.com/open-eid/SiVa/tree/master/test-helpers).\n\n")
Test_cases.write("The structure and elements of test case is described in [QA Strategy](http://open-eid.github.io/SiVa/siva2/qa_strategy/#testing) document.\n")
Test_cases.write("All the files used in the tests can be found in [SiVa GitHub](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/resources).\n")

new_file = 1
        
for root, dirs, filenames in os.walk(source_in_dir):
    for f in filenames:
        a_file = open(os.path.join(root,f), 'r', encoding="utf8")
        new_file = 1

        for a_line in a_file:

            if '* Note:' in a_line:
                if (new_file):
                    Test_cases.write("\n## "+f+"\n\n")   
                    Test_cases.write("!!! Note\n\n")
                    new_file = 0
                Test_cases.write("    "+a_line.lstrip("* Note:").rstrip()+"\n")
            
            if '* TestCaseID:' in a_line:
                if (new_file):
                    Test_cases.write("\n## "+f+"\n")
                    correct_folder = os.path.relpath(root,os.path.dirname(root))
                    Test_cases.write("[Open file](https://github.com/open-eid/SiVa/tree/master/siva-parent/siva-test/src/test/java/ee/openeid/siva/"+correct_folder+"/"+f+")\n\n")
                    new_file = 0
                Test_cases.write("\n**"+a_line.lstrip("* ").rstrip()+"**\n\n")
            elif '* TestType:' in a_line:
                Test_cases.write("  "+a_line.lstrip().rstrip()+"\n")
            elif '* Requirement:' in a_line:
                Test_cases.write("  * Requirement: ["+a_line[20:].rstrip()+"]("+a_line[20:].rstrip()+")\n")
            elif '* Title:' in a_line:
                Test_cases.write("  "+a_line.lstrip().rstrip()+"\n")
            elif '* Expected Result:' in a_line:
                Test_cases.write("  "+a_line.lstrip().rstrip()+"\n")
            elif '* File:' in a_line:
                Test_cases.write("  "+a_line.lstrip().rstrip()+"\n\n")
            elif '@Ignore' in a_line:
                a_line = a_line.replace("@Ignore","")
                if '//TODO:' in a_line:
                    Test_cases.write("  **Attention! This test is disabled: "+a_line[12:].strip()+"\n\n")
                else:
                    Test_cases.write("  **Attention! This test is disabled: "+a_line.strip()+"\n\n")
        a_file.close()
Test_cases.close()
            



